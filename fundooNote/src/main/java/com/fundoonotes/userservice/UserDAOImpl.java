package com.fundoonotes.userservice;


/*@Repository
public class UserDAOImpl implements UserDAO 
{

	@Override
	public boolean save(User user) {
		return false;
	}
	@Autowired
	private JdbcTemplate jdbcTemplate;

	private final String SQL_INSERT_USER = "INSERT INTO USERS(fullName, email, mobile, password) values(?, ?, ?, ?)";
	
	@Override
	public boolean save(User user) 
	{
		
		int result = jdbcTemplate.update(SQL_INSERT_USER, user.getFullName(), user.getEmail(), 
														user.getMobile(), user.getPassword());
		return result > 0;
	}
	
	private static final class UserMapper implements RowMapper<User>
	{
		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException 
		{
			User user = new User();
			user.setId(rs.getString("id"));
			user.setFullName(rs.getString("fullname"));
			user.setEmail(rs.getString("email")); 
			user.setMobile(Long.parseLong(rs.getString("mobile")));
			return null;
		}
		
	}
	
	
}*/
