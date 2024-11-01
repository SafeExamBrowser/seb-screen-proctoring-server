package ch.ethz.seb.sps.domain.model.service;

public record UserListForApplicationSearch(
        String username,
        String sessionUuid,
        Long firstScreenshotCaptureTime,
        Integer count
){
}

