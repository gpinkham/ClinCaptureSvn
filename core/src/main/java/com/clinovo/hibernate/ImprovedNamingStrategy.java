package com.clinovo.hibernate;

import org.apache.commons.lang.StringUtils;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class ImprovedNamingStrategy implements PhysicalNamingStrategy {

	public Identifier toPhysicalCatalogName(Identifier identifier, JdbcEnvironment jdbcEnv) {
		return convert(identifier);
	}

	public Identifier toPhysicalColumnName(Identifier identifier, JdbcEnvironment jdbcEnv) {
		return convert(identifier);
	}

	public Identifier toPhysicalSchemaName(Identifier identifier, JdbcEnvironment jdbcEnv) {
		return convert(identifier);
	}

	public Identifier toPhysicalSequenceName(Identifier identifier, JdbcEnvironment jdbcEnv) {
		return convert(identifier);
	}

	public Identifier toPhysicalTableName(Identifier identifier, JdbcEnvironment jdbcEnv) {
		return convert(identifier);
	}

	private Identifier convert(Identifier identifier) {
		if (identifier == null || StringUtils.isBlank(identifier.getText())) {
			return identifier;
		}

		String regex = "([a-z])([A-Z])";
		String replacement = "$1_$2";
		String newName = identifier.getText().replaceAll(regex, replacement).toLowerCase();
		return Identifier.toIdentifier(newName);
	}
}