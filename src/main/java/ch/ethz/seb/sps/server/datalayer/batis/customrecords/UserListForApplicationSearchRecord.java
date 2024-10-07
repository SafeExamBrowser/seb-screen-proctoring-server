package ch.ethz.seb.sps.server.datalayer.batis.customrecords;

public record UserListForApplicationSearchRecord(
        String username,
        String sessionUuid,
        Long firstScreenshotCaptureTime,
        Integer count
){
}

