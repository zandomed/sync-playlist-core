package com.zandome.syncplaylist.user.infra.adapters.http.dtos.response;

import java.util.List;

public record AuthMethodsHttpResponse(List<String> providers) {
}