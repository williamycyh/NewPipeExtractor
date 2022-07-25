package org.schabi.newpipe.downloader;

import com.ppd.ersan.video.downloader.Request;
import com.ppd.ersan.video.downloader.Response;

final class TestRequestResponse {
    private final Request request;
    private final Response response;

    public TestRequestResponse(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
