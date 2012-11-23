package co.freeside.hipsteroid.auth

class User {

	transient springSecurityService

	String username
	String password
	boolean enabled
	boolean accountExpired
	boolean accountLocked
	boolean passwordExpired

	static hasMany = [oAuthIDs: OAuthID]

	static constraints = {
		username blank: false, unique: true
		password blank: false
	}

	static mapping = {
		password column: '`password`'
	}

	Set<Role> getAuthorities() {
		UserRole.findAllByUser(this).collect { it.role } as Set
	}

	def beforeInsert() {
		encodePassword()
	}

	def beforeUpdate() {
		if (isDirty('password')) {
			encodePassword()
		}
	}

	protected void encodePassword() {
		password = springSecurityService.encodePassword(password)
	}

	private boolean isDirty(String propertyName) {
		def persistedValue = User.collection.findOne(id)[propertyName]
		def newValue = this[propertyName]
		println "old value: $persistedValue, new value: $newValue"
		persistedValue != newValue
	}

}
