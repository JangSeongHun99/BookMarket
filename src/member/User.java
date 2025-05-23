package member;

public class User extends Person {
	private String userId;
	private String password;
	private String role; // 역할 필드 추가

	// 생성자 수정: role 파라미터 추가
	public User(String userId, String password, String name, int phone, String address, String role) {
		super(name, phone, address);
		this.userId = userId;
		this.password = password;
		this.role = role; // 역할 설정
	}

	// 일반 사용자 회원가입 시 사용할 수 있는 생성자 (role은 "USER"로 고정)
	public User(String userId, String password, String name, int phone, String address) {
		this(userId, password, name, phone, address, "USER"); // 기본 역할은 USER
	}

	// 로그인 시 사용할 수 있는 생성자 (role 정보는 DB에서 가져옴)
	public User(String userId, String password) {
		this(userId, password, null, 0, null, null); // 이름, 전화번호, 주소, 역할은 나중에 설정
	}


	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRole() { // role getter 추가
		return role;
	}
	public void setRole(String role) { // role setter 추가
		this.role = role;
	}
}