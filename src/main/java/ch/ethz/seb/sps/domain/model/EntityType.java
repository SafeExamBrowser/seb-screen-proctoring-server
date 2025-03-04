package ch.ethz.seb.sps.domain.model;

import jakarta.annotation.Generated;

@Generated(value="org.mybatis.generator.api.MyBatisGenerator",comments="ch.ethz.seb.sps.generator.DomainModelNameReferencePlugin",date="2024-11-18T10:46:20.589+01:00")
public enum EntityType {
    USER,
    CLIENT_ACCESS,
    EXAM,
    EXAM_SEB_RESTRICTION,
    EXAM_PROCTOR_DATA,
    SEB_GROUP,
    SESSION,
    SCREENSHOT_DATA,
    SCREENSHOT,
    ENTITY_PRIVILEGE,
    ADDITIONAL_ATTRIBUTE,
    WEBSERVICE_SERVER_INFO,
    AUDIT_LOG;
}