include "domain.thrift"

namespace java com.ecomnext.services

service SecurityService {

	domain.TUser getUser(1:string username) throws (1:domain.TDataAccessException dae, 2:domain.TNotFoundException nf),

	list<domain.TUser> getUsers() throws (1:domain.TDataAccessException dae),

	i32 countUsersByRole(1:bool enabled, 2:list<string> roles) throws (1:domain.TDataAccessException dae),

	void cleanUpOldUsers() throws (1:domain.TDataAccessException dae)

}