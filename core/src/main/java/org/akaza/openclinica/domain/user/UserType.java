package org.akaza.openclinica.domain.user;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.akaza.openclinica.domain.DataMapDomainObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
* UserType.
*/
@Entity
@Table(name = "user_type")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence", value = "user_type_user_type_id_seq") })

public class UserType  extends DataMapDomainObject implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private int userTypeId;
	private String userType;
	private Set<UserAccount> userAccounts = new HashSet<UserAccount>(0);

	@Id
	@Column(name = "user_type_id", unique = true, nullable = false)
	public int getUserTypeId() {
		return this.userTypeId;
	}

	public void setUserTypeId(int userTypeId) {
		this.userTypeId = userTypeId;
	}

	@Column(name = "user_type", length = 50)
	public String getUserType() {
		return this.userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "userType")
	public Set<UserAccount> getUserAccounts() {
		return this.userAccounts;
	}

	public void setUserAccounts(Set<UserAccount> userAccounts) {
		this.userAccounts = userAccounts;
	}

}

