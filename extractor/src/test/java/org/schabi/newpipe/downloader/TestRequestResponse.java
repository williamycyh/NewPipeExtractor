package org.schabi.newpipe.downloader;

import tndown.tndir.simplevd.vdwload.downloader.Request;
import tndown.tndir.simplevd.vdwload.downloader.Response;

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
