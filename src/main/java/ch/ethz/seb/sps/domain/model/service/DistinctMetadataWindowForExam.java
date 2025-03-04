package ch.ethz.seb.sps.domain.model.service;

import java.util.Collection;

public record DistinctMetadataWindowForExam(
        Long totalAmount,
        Collection<String> distinctWindowTitles) {
}