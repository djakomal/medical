package medico.PPE.Repositories;

import medico.PPE.Models.AppointementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointementTypeRepository  extends JpaRepository<AppointementType,Long >{
}
