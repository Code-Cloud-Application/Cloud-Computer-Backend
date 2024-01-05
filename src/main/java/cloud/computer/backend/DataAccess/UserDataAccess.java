package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.Exception.UserAlreadyExistException;
import cloud.computer.backend.Entity.RowMapper.UserRowMapper;
import cloud.computer.backend.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDataAccess implements GenericIdDataAccess{

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

    /**
     * 判断某位用户是否存在
     * @param username 用户名
     * @return 如果存在则返回true，否则返回false
     */
    public boolean isExist(String username){
        return !this.template.query("select * from `user` where username=?", new UserRowMapper(), username).isEmpty();
    }

    /**
     * 判断某位用户是否存在。
     * @apiNote 本质上是使用用户名进行判断
     * @param user 用户实体
     * @return 如果存在则返回true，否则返回false
     */
    public boolean isExist(User user){
        return isExist(user.getUsername());
    }

    /**
     * 取出用户实体
     * @param id 用户ID
     * @return 返回用户实体。如果用户不存在，则返回null。
     */

    @Nullable
    public User getUser(int id){
        try {
            return this.template.queryForObject("select * from `user` where id=?", new UserRowMapper(), id);
        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    @Nullable
    public User getUser(String username){
        try {
            return this.template.queryForObject("select * from `user` where username=?", new UserRowMapper(), username);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
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

    /**
     * 删除某位用户
     * @param id 用户ID
     */
    public void removeUser(int id){
        this.template.update("delete from user where id=?;", id);
    }

    /**
     * 更新某位用户的用户信息
     * @apiNote 根据ID筛选
     * @param user 新的用户实体
     */
    public void updateUser(User user){
        this.template.update("update user set username=?, password=? where id=?;",
                user.getUsername(), user.getPassword(), user.getId());
    }

    @Override
    public int getMaxId() {
        Optional<Integer> optional = Optional.ofNullable(this.template.queryForObject("select MAX(id) from `user`", Integer.class));
        return optional.orElse(0);
    }
}
