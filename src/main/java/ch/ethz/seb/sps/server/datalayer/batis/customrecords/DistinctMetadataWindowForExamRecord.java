package ch.ethz.seb.sps.server.datalayer.batis.customrecords;

import java.util.Collection;

public record DistinctMetadataWindowForExamRecord(
        Long totalAmount,
        Collection<String> distinctWindowTitles) {
}