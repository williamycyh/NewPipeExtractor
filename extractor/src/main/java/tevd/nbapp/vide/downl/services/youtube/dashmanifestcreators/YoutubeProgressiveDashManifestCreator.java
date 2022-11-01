package tevd.nbapp.vide.downl.services.youtube.dashmanifestcreators;

import tevd.nbapp.vide.downl.services.youtube.DeliveryType;
import tevd.nbapp.vide.downl.services.youtube.ItagItem;
import tevd.nbapp.vide.downl.utils.ManifestCreatorCache;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Class which generates DASH manifests of {@link DeliveryType#PROGRESSIVE YouTube progressive}
 * streams.
 */
public final class YoutubeProgressiveDashManifestCreator {

    /**
     * Cache of DASH manifests generated for progressive streams.
     */
    private static final ManifestCreatorCache<String, String> PROGRESSIVE_STREAMS_CACHE
            = new ManifestCreatorCache<>();

    private YoutubeProgressiveDashManifestCreator() {
    }

    /**
     * Create DASH manifests from a YouTube progressive stream.
     *
     * <p>
     * Progressive streams are YouTube DASH streams which work with range requests and without the
     * need to get a manifest.
     * </p>
     *
     * <p>
     * They can be found on all videos, and for all streams for most of videos which come from a
     * YouTube partner, and on videos with a large number of views.
     * </p>
     *
     * <p>This method needs:
     *     <ul>
     *         <li>the base URL of the stream (which, if you try to access to it, returns the whole
     *         stream, after redirects, and if the URL is valid);</li>
     *         <li>an {@link ItagItem}, which needs to contain the following information:
     *              <ul>
     *                  <li>its type (see {@link ItagItem.ItagType}), to identify if the content is
     *                  an audio or a video stream;</li>
     *                  <li>its bitrate;</li>
     *                  <li>its mime type;</li>
     *                  <li>its codec(s);</li>
     *                  <li>for an audio stream: its audio channels;</li>
     *                  <li>for a video stream: its width and height.</li>
     *              </ul>
     *         </li>
     *         <li>the duration of the video (parameter {@code durationSecondsFallback}), which
     *         will be used as the stream duration if the duration could not be parsed from the
     *         {@link ItagItem}.</li>
     *     </ul>
     * </p>
     *
     * @param progressiveStreamingBaseUrl the base URL of the progressive stream, which must not be
     *                                    null
     * @param itagItem                    the {@link ItagItem} corresponding to the stream, which
     *                                    must not be null
     * @param durationSecondsFallback     the duration of the progressive stream which will be used
     *                                    if the duration could not be extracted from the
     *                                    {@link ItagItem}
     * @return the manifest generated into a string
     */
    @Nonnull
    public static String fromProgressiveStreamingUrl(
            @Nonnull final String progressiveStreamingBaseUrl,
            @Nonnull final ItagItem itagItem,
            final long durationSecondsFallback) throws CreationException {
        if (PROGRESSIVE_STREAMS_CACHE.containsKey(progressiveStreamingBaseUrl)) {
            return Objects.requireNonNull(
                    PROGRESSIVE_STREAMS_CACHE.get(progressiveStreamingBaseUrl)).getSecond();
        }

        final long itagItemDuration = itagItem.getApproxDurationMs();
        final long streamDuration;
        if (itagItemDuration != -1) {
            streamDuration = itagItemDuration;
        } else {
            if (durationSecondsFallback > 0) {
                streamDuration = durationSecondsFallback * 1000;
            } else {
                throw CreationException.couldNotAddElement(YoutubeDashManifestCreatorsUtils.MPD, "the duration of the stream "
                        + "could not be determined and durationSecondsFallback is <= 0");
            }
        }

        final Document doc = YoutubeDashManifestCreatorsUtils.generateDocumentAndDoCommonElementsGeneration(itagItem,
                streamDuration);

        generateBaseUrlElement(doc, progressiveStreamingBaseUrl);
        generateSegmentBaseElement(doc, itagItem);
        generateInitializationElement(doc, itagItem);

        return YoutubeDashManifestCreatorsUtils.buildAndCacheResult(progressiveStreamingBaseUrl, doc,
                PROGRESSIVE_STREAMS_CACHE);
    }

    /**
     * @return the cache of DASH manifests generated for progressive streams
     */
    @Nonnull
    public static ManifestCreatorCache<String, String> getCache() {
        return PROGRESSIVE_STREAMS_CACHE;
    }

    /**
     * Generate the {@code <BaseURL>} element, appended as a child of the
     * {@code <Representation>} element.
     *
     * <p>
     * The {@code <Representation>} element needs to be generated before this element with
     * {@link YoutubeDashManifestCreatorsUtils#generateRepresentationElement(Document, ItagItem)}).
     * </p>
     *
     * @param doc the {@link Document} on which the {@code <BaseURL>} element will be appended
     * @param baseUrl  the base URL of the stream, which must not be null and will be set as the
     *                 content of the {@code <BaseURL>} element
     */
    private static void generateBaseUrlElement(@Nonnull final Document doc,
                                               @Nonnull final String baseUrl)
            throws CreationException {
        try {
            final Element representationElement = (Element) doc.getElementsByTagName(
                    YoutubeDashManifestCreatorsUtils.REPRESENTATION).item(0);
            final Element baseURLElement = doc.createElement(YoutubeDashManifestCreatorsUtils.BASE_URL);
            baseURLElement.setTextContent(baseUrl);
            representationElement.appendChild(baseURLElement);
        } catch (final DOMException e) {
            throw CreationException.couldNotAddElement(YoutubeDashManifestCreatorsUtils.BASE_URL, e);
        }
    }

    /**
     * Generate the {@code <SegmentBase>} element, appended as a child of the
     * {@code <Representation>} element.
     *
     * <p>
     * It generates the following element:
     * <br>
     * {@code <SegmentBase indexRange="indexStart-indexEnd" />}
     * <br>
     * (where {@code indexStart} and {@code indexEnd} are gotten from the {@link ItagItem} passed
     * as the second parameter)
     * </p>
     *
     * <p>
     * The {@code <Representation>} element needs to be generated before this element with
     * {@link YoutubeDashManifestCreatorsUtils#generateRepresentationElement(Document, ItagItem)}),
     * and the {@code BaseURL} element with {@link #generateBaseUrlElement(Document, String)}
     * should be generated too.
     * </p>
     *
     * @param doc the {@link Document} on which the {@code <SegmentBase>} element will be appended
     * @param itagItem the {@link ItagItem} to use, which must not be null
     */
    private static void generateSegmentBaseElement(@Nonnull final Document doc,
                                                   @Nonnull final ItagItem itagItem)
            throws CreationException {
        try {
            final Element representationElement = (Element) doc.getElementsByTagName(
                    YoutubeDashManifestCreatorsUtils.REPRESENTATION).item(0);
            final Element segmentBaseElement = doc.createElement(YoutubeDashManifestCreatorsUtils.SEGMENT_BASE);

            final String range = itagItem.getIndexStart() + "-" + itagItem.getIndexEnd();
            if (itagItem.getIndexStart() < 0 || itagItem.getIndexEnd() < 0) {
                throw CreationException.couldNotAddElement(YoutubeDashManifestCreatorsUtils.SEGMENT_BASE,
                        "ItagItem's indexStart or " + "indexEnd are < 0: " + range);
            }
            YoutubeDashManifestCreatorsUtils.setAttribute(segmentBaseElement, doc, "indexRange", range);

            representationElement.appendChild(segmentBaseElement);
        } catch (final DOMException e) {
            throw CreationException.couldNotAddElement(YoutubeDashManifestCreatorsUtils.SEGMENT_BASE, e);
        }
    }

    /**
     * Generate the {@code <Initialization>} element, appended as a child of the
     * {@code <SegmentBase>} element.
     *
     * <p>
     * It generates the following element:
     * <br>
     * {@code <Initialization range="initStart-initEnd"/>}
     * <br>
     * (where {@code indexStart} and {@code indexEnd} are gotten from the {@link ItagItem} passed
     * as the second parameter)
     * </p>
     *
     * <p>
     * The {@code <SegmentBase>} element needs to be generated before this element with
     * {@link #generateSegmentBaseElement(Document, ItagItem)}).
     * </p>
     *
     * @param doc the {@link Document} on which the {@code <Initialization>} element will be
     *            appended
     * @param itagItem the {@link ItagItem} to use, which must not be null
     */
    private static void generateInitializationElement(@Nonnull final Document doc,
                                                      @Nonnull final ItagItem itagItem)
            throws CreationException {
        try {
            final Element segmentBaseElement = (Element) doc.getElementsByTagName(
                    YoutubeDashManifestCreatorsUtils.SEGMENT_BASE).item(0);
            final Element initializationElement = doc.createElement(YoutubeDashManifestCreatorsUtils.INITIALIZATION);

            final String range = itagItem.getInitStart() + "-" + itagItem.getInitEnd();
            if (itagItem.getInitStart() < 0 || itagItem.getInitEnd() < 0) {
                throw CreationException.couldNotAddElement(YoutubeDashManifestCreatorsUtils.INITIALIZATION,
                        "ItagItem's initStart and/or " + "initEnd are/is < 0: " + range);
            }
            YoutubeDashManifestCreatorsUtils.setAttribute(initializationElement, doc, "range", range);

            segmentBaseElement.appendChild(initializationElement);
        } catch (final DOMException e) {
            throw CreationException.couldNotAddElement(YoutubeDashManifestCreatorsUtils.INITIALIZATION, e);
        }
    }
}
