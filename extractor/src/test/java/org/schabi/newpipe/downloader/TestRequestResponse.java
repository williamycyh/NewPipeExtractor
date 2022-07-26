package org.schabi.newpipe.downloader;

import vmeno.yyml.nnbersi.downd.downloader.Request;
import vmeno.yyml.nnbersi.downd.downloader.Response;

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
