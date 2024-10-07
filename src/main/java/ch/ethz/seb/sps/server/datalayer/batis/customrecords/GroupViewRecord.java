package ch.ethz.seb.sps.server.datalayer.batis.customrecords;

public record GroupViewRecord(
        Long id,
        String uuid,
        String name,
        String description,
        String owner,
        Long creationTime,
        Long lastUpdateTime,
        Long terminationTime,
        String examUuid,
        String examName,
        Long examStartTime,
        Long examEndTime) {
}
