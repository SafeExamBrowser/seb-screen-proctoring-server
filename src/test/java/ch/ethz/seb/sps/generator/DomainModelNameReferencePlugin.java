/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.InnerInterface;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelEnumeration;
import org.mybatis.generator.config.PropertyRegistry;

public class DomainModelNameReferencePlugin extends PluginAdapter {

    private static final String DOMAIN_FQ_NAME = "ch.ethz.seb.sps.domain.Domain";
    private static final String DOMAIN_ENUM_FQ_NAME = "ch.ethz.seb.sps.domain.model.EntityType";
    private static final String DOMAIN_INTERFACE = "DOMAIN_INTERFACE";
    private static final String DOMAIN_ENUMERATION = "DOMAIN_ENUMERATION";

    @Override
    public boolean validate(final List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(
            final IntrospectedTable introspectedTable) {

        final List<GeneratedJavaFile> result = new ArrayList<>();
        if (!this.properties.containsKey(DOMAIN_INTERFACE)) {
            createDomainInterface(result);
        }

        final Interface domain = (Interface) this.properties.get(DOMAIN_INTERFACE);

        final TopLevelEnumeration entityType = (TopLevelEnumeration) this.properties.get(DOMAIN_ENUMERATION);

        final String typeName = toTypeName(introspectedTable.getFullyQualifiedTableNameAtRuntime(), true);
        final String interfaceName = StringUtils.join(
                Arrays.asList(
                        StringUtils.splitByCharacterTypeCamelCase(typeName))
                        .stream()
                        .map(s -> s.toUpperCase())
                        .collect(Collectors.toList()),
                "_");

        entityType.addEnumConstant(interfaceName);
        addAdditionalEntityTypes(entityType, interfaceName);

        final InnerInterface innerInterface = new InnerInterface(new FullyQualifiedJavaType(interfaceName));

        final Field field = new Field(
                "TYPE_NAME",
                FullyQualifiedJavaType.getStringInstance());
        field.setInitializationString("\"" + typeName + "\"");
        innerInterface.addField(field);

        final String refName = toTypeName(introspectedTable.getFullyQualifiedTableNameAtRuntime(), false) + "s";
        final Field refField = new Field(
                "REFERENCE_NAME",
                FullyQualifiedJavaType.getStringInstance());
        refField.setInitializationString("\"" + refName + "\"");
        innerInterface.addField(refField);

        final List<IntrospectedColumn> baseColumns = introspectedTable.getAllColumns();
        if (baseColumns != null) {
            for (final IntrospectedColumn column : baseColumns) {
                final Field columnNameField = new Field(
                        "ATTR_" + column.getActualColumnName().toUpperCase(),
                        FullyQualifiedJavaType.getStringInstance());
                columnNameField.setInitializationString("\"" + toTypeName(column.getActualColumnName(), false) + "\"");
                innerInterface.addField(columnNameField);
            }
        }

        domain.addInnerInterfaces(innerInterface);

        return result;
    }

    private void addAdditionalEntityTypes(final TopLevelEnumeration entityType, final String interfaceName) {
        if ("EXAM".equals(interfaceName)) {
            entityType.addEnumConstant(interfaceName + "_SEB_RESTRICTION");
            entityType.addEnumConstant(interfaceName + "_PROCTOR_DATA");
        }
    }

    private String toTypeName(final String tableName, final boolean upperCamelCase) {
        final String[] split = StringUtils.split(tableName, "_");
        final StringBuilder builder = new StringBuilder();
        if (split != null) {
            for (int i = 0; i < split.length; i++) {
                final char[] charArray = split[i].toCharArray();
                if (upperCamelCase || i > 0) {
                    charArray[0] = Character.toUpperCase(charArray[0]);
                }
                builder.append(charArray);
            }
        }
        return builder.toString();
    }

    private void createDomainInterface(final List<GeneratedJavaFile> result) {
        final String targetProject = this.context
                .getJavaClientGeneratorConfiguration()
                .getTargetProject();

        final Interface domain = new Interface(DOMAIN_FQ_NAME);
        domain.setVisibility(JavaVisibility.PUBLIC);
        domain.addImportedType(new FullyQualifiedJavaType("javax.annotation.Generated"));
        domain.addAnnotation(
                "@Generated(value=\"org.mybatis.generator.api.MyBatisGenerator\","
                        + "comments=\"ch.ethz.seb.sps.generator.DomainModelNameReferencePlugin\","
                        + "date=\"" + DateTime.now().toString() + "\")");
        domain.addJavaDocLine("/** Defines the global names of the domain model and domain model fields.");
        domain.addJavaDocLine(
                "* This shall be used as a static overall domain model names reference within SEB Server Web-Service as well as within the integrated GUI");
        domain.addJavaDocLine(
                "* This file is generated by the ch.ethz.seb.sps.generator.DomainModelNameReferencePlugin and must not be edited manually.**/");

        GeneratedJavaFile javaFile = new GeneratedJavaFile(
                domain,
                targetProject,
                this.context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                this.context.getJavaFormatter());

        this.properties.put(DOMAIN_INTERFACE, domain);
        result.add(javaFile);

        final TopLevelEnumeration entityType = new TopLevelEnumeration(new FullyQualifiedJavaType(DOMAIN_ENUM_FQ_NAME));
        entityType.setVisibility(JavaVisibility.PUBLIC);
        this.properties.put(DOMAIN_ENUMERATION, entityType);
        entityType.addImportedType(new FullyQualifiedJavaType("javax.annotation.Generated"));
        entityType.addAnnotation(
                "@Generated(value=\"org.mybatis.generator.api.MyBatisGenerator\","
                        + "comments=\"ch.ethz.seb.sps.generator.DomainModelNameReferencePlugin\","
                        + "date=\"" + DateTime.now().toString() + "\")");

        javaFile = new GeneratedJavaFile(
                entityType,
                targetProject,
                this.context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                this.context.getJavaFormatter());

        result.add(javaFile);
    }

}
