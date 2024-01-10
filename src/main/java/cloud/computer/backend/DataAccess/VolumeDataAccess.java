package cloud.computer.backend.DataAccess;

import cloud.computer.backend.Entity.RowMapper.VolumeRowMapper;
import cloud.computer.backend.Entity.Volume;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class VolumeDataAccess {
    private final JdbcTemplate template;

    public VolumeDataAccess(JdbcTemplate template) {
        this.template = template;
    }

    public List<Volume> getVolumes(){
        return this.template.query("select * from volume;", new VolumeRowMapper());
    }

    @Nullable
    public Volume getVolume(String id){
        try {
            return this.template.queryForObject("select * from volume where id=?", new VolumeRowMapper(), id);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void addVolume(Volume volume){
        this.template.update("insert into volume (id, desktop_id, size, bootable) values (?, ?, ?, ?);",
                volume.getId(), volume.getDesktopId(), volume.getSize(), (short) (volume.isBootable() ? 1: 0));
    }

    public void removeVolume(String id){
        this.template.update("delete from volume where id=?;", id);
    }
    
    public void updateVolume(Volume volume){
        this.template.update("update volume set size = ?, desktop_id = ?, bootable = ? where id=?;",
                volume.getSize(), volume.getDesktopId(), (short) (volume.isBootable()? 1: 0), volume.getId());
    }

    public List<Volume> getByDesktopId(String desktopId){
        return this.template.query("select * from volume where desktop_id=?;", new VolumeRowMapper(), desktopId);
    }
}
