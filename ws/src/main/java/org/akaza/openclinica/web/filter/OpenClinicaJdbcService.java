/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.web.filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.MappingSqlQuery;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

@SuppressWarnings({"rawtypes", "unchecked", "deprecation"})
public class OpenClinicaJdbcService extends JdbcDaoImpl {

	private MappingSqlQuery ocUsersByUsernameMapping;

	/**
	 * Executes the <tt>usersByUsernameQuery</tt> and returns a list of UserDetails objects (there should normally only
	 * be one matching user).
	 */
	@Override
	protected List loadUsersByUsername(String username) {
		this.ocUsersByUsernameMapping = new OcUsersByUsernameMapping(getDataSource());
		return ocUsersByUsernameMapping.execute(username);
	}

	/**
	 * Can be overridden to customize the creation of the final UserDetailsObject returnd from
	 * <tt>loadUserByUsername</tt>.
	 * 
	 * @param username
	 *            the name originally passed to loadUserByUsername
	 * @param userFromUserQuery
	 *            the object returned from the execution of the
	 * @param combinedAuthorities
	 *            the combined array of authorities from all the authority loading queries.
	 * @return the final UserDetails which should be used in the system.
	 */
	protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
			Collection<? extends GrantedAuthority> combinedAuthorities) {
		String returnUsername = userFromUserQuery.getUsername();

		if (!isUsernameBasedPrimaryKey()) {
			returnUsername = username;
		}

		return new User(returnUsername, userFromUserQuery.getPassword(), userFromUserQuery.isEnabled(), true, true,
				userFromUserQuery.isAccountNonLocked(), combinedAuthorities);
	}

	/**
	 * Query object to look up a user.
	 */
	private class OcUsersByUsernameMapping extends MappingSqlQuery {
		protected OcUsersByUsernameMapping(DataSource ds) {
			super(ds, getUsersByUsernameQuery());
			declareParameter(new SqlParameter(Types.VARCHAR));
			compile();
		}

		@Override
		protected Object mapRow(ResultSet rs, int rownum) throws SQLException {
			String username = rs.getString(1);
			String password = rs.getString(2);
			boolean enabled = rs.getBoolean(3);
			boolean nonLocked = rs.getBoolean(4);
			ArrayList<GrantedAuthority> newAuthorities = new ArrayList<GrantedAuthority>();
			newAuthorities.add(new GrantedAuthorityImpl("HOLDER"));
			UserDetails user = new User(username, password, enabled, true, true, nonLocked, newAuthorities);

			return user;
		}
	}

}
