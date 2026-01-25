package medico.PPE.Repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import medico.PPE.Models.ZoomMeeting;


@Repository
public interface ZoomMeetingRepository extends JpaRepository<ZoomMeeting, String> {
}
