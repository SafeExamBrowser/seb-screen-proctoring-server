package ch.ethz.seb.sps.server.datalayer.batis.customrecords;

public class GroupViewRecord {

    private final Long id;
    private final String uuid;
    private final String name;
    private final String description;
    private final String owner;
    private final Long creationTime;
    private final Long lastUpdateTime;
    private final Long terminationTime;
    private final String examUuid;
    private final String examName;
    private final Long examStartTime;
    private final Long examEndTime;

    public GroupViewRecord(
            final Long id,
            final String uuid,
            final String name,
            final String description,
            final String owner,
            final Long creationTime,
            final Long lastUpdateTime, Long terminationTime,
            final String examUuid,
            final String examName,
            final Long examStartTime,
            final Long examEndTime
    ) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.examUuid = examUuid;
        this.examName = examName;
        this.examStartTime = examStartTime;
        this.examEndTime = examEndTime;
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Long getTerminationTime() {
        return terminationTime;
    }

    public String getExamUuid() {
        return examUuid;
    }

    public String getExamName() {
        return examName;
    }
    public Long getExamStartTime() {
        return examStartTime;
    }
    public Long getExamEndTime() {
        return examEndTime;
    }
}
