namespace java com.ecomnext.domain

enum TUserType {
	EMPLOYEE,
	EXTERNAL
}

struct TUser {
	1: string username,
	2: string password,
	3: list<string> roles,
	4: bool enabled,
	5: TUserType userType
}

exception TNotFoundException {
	1:string msg,
}

exception TDataAccessException {
	1:string msg;
}
