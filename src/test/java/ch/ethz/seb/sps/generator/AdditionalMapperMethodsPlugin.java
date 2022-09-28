/*
 * Copyright (c) 2018 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.generator;

import java.util.List;

import org.mybatis.dynamic.sql.select.MyBatis3SelectModelAdapter;
import org.mybatis.dynamic.sql.select.QueryExpressionDSL;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** This plugin adds a generic "selectIds" method to all generated mappers that returns a list of Long values and a
 * "selectIdsByExample" method to get an selection sql DSL builder for the specified table.
 *
 * NOTE: this was for testing how MyBatis generator plugins are working
 *
 * TODO: It would be nice to have a MyBatis generator plugin that generates also separate domain model names reference
 * in the form of nested interfaces like:
 *
 * interface DomainModel {
 *
 * interface Institution {
 *
 * String ID = "id"; String NAME = "name"; ... }
 *
 * interface LmsSetup { String ID = "id"; String NAME = "name"; ... }
 *
 * interface User { String ID = "id"; String NAME = "name"; ... }
 *
 * ...
 *
 * }
 *
 * @author anhefti */
public class AdditionalMapperMethodsPlugin extends PluginAdapter {

    private static final Logger log = LoggerFactory.getLogger(AdditionalMapperMethodsPlugin.class);

    @Override
    public boolean validate(final List<String> warnings) {
        return true;
    }

    @Override
    public boolean clientGenerated(
            final Interface interfaze,
            final TopLevelClass topLevelClass,
            final IntrospectedTable introspectedTable) {

        final boolean clientGenerated = super.clientGenerated(interfaze, topLevelClass, introspectedTable);

        log.info(
                "Create additional mapper methods; selectIds and selectIdsByExample for mapper: "
                        + interfaze.getType());

        final Method selectIds = new Method("selectIds");
        selectIds.addAnnotation(
                "@Generated(value=\"org.mybatis.generator.api.MyBatisGenerator\")");
        selectIds.addAnnotation("@SelectProvider(type=SqlProviderAdapter.class, method=\"select\")");
        selectIds.addAnnotation(
                "@ConstructorArgs({@Arg(column=\"id\", javaType=Long.class, jdbcType=JdbcType.BIGINT, id=true)})");
        final FullyQualifiedJavaType newListInstance = FullyQualifiedJavaType.getNewListInstance();
        newListInstance.addTypeArgument(new FullyQualifiedJavaType(Long.class.getName()));
        selectIds.setReturnType(newListInstance);
        selectIds.addParameter(new Parameter(new FullyQualifiedJavaType(
                SelectStatementProvider.class.getName()),
                "select"));

        String tableRecordRefName = introspectedTable.getTableConfiguration().getDomainObjectName();
        final Character firstChar = Character.toLowerCase(tableRecordRefName.charAt(0));
        tableRecordRefName = firstChar + tableRecordRefName.substring(1, tableRecordRefName.length());
        final Method selectIdsByExample = new Method("selectIdsByExample");
        final FullyQualifiedJavaType returnType = new FullyQualifiedJavaType(QueryExpressionDSL.class.getName());
        final FullyQualifiedJavaType type = new FullyQualifiedJavaType(MyBatis3SelectModelAdapter.class.getName());
        type.addTypeArgument(newListInstance);
        returnType.addTypeArgument(type);
        selectIdsByExample.setReturnType(returnType);
        selectIdsByExample.setDefault(true);
        selectIdsByExample.addBodyLine("return SelectDSL.selectDistinctWithMapper(this::selectIds, id)");
        selectIdsByExample.addBodyLine("                .from(" + tableRecordRefName + ");");

        interfaze.addMethod(selectIds);
        interfaze.addMethod(selectIdsByExample);

        final FullyQualifiedJavaType fullyQualifiedJavaType = interfaze.getType();
        if (fullyQualifiedJavaType.getShortName().equals("ClientEventRecordMapper")) {
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.ConstructorArgs"));
            interfaze.addImportedType(new FullyQualifiedJavaType("org.apache.ibatis.annotations.Arg"));
        }

        return clientGenerated;
    }

}
