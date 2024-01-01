package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.Exception.UserAlreadyExistException;
import cloud.computer.backend.Entity.RowMapper.UserRowMapper;
import cloud.computer.backend.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDataAccess {

    private JdbcTemplate template;

    @Autowired
    private void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    /**
     * 取出所有用户
     * @return 所有用户构成的列表
     */
    public List<User> getUsers(){
        return this.template.query("SELECT * FROM `user`", new UserRowMapper());
    }

    public boolean isExist(int id){
        return !this.template.query("select * from `user` where id=?", new UserRowMapper(), id).isEmpty();
    }

    public boolean isExist(User user){
        return isExist(user.getId());
    }

    public User getUser(int id){
        return this.template.queryForObject("select * from `user` where id=?", new UserRowMapper(), id);
    }

    /**
     * 添加用户
     * @param user 要添加的用户
     */
    public void addUser(User user) throws UserAlreadyExistException {
        if (isExist(user)) {
            UserAlreadyExistException exception = new UserAlreadyExistException();
            exception.setUser(user);
            exception.setDuplicateUser(getUser(user.getId()));
            throw exception;
        }
        this.template.update("insert into user (id, username, password) values (?, ?, ?);",
                user.getId(), user.getUsername(), user.getPassword());
    }

    public void removeUser(int id){
        this.template.update("delete from user where id=?;", id);
    }

    public void updateUser(User user){
        this.template.update("update user set username=?, password=? where id=?;",
                user.getUsername(), user.getPassword(), user.getId());
    }

}
