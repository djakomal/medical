package medico.PPE.Services;

import jakarta.transaction.Transactional;

import medico.PPE.Models.User;
import medico.PPE.Repositories.UserRepository;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static medico.PPE.Mappers.Userdtomapper.userdtomapper;
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;



    @Override
    public List<User> getAll() {
            return userRepository.findAll();
    }

    @Override
    @Transactional
    public User add(User user) {
        if(user==null){
            throw new IllegalArgumentException("User cannot be null");
        }
        try {
            //User user = userdtomapper.mapToUser(userDto);
            User savedUser = userRepository.save(user);
            //UserDto savedUserDto = Userdtomapper.userdtomapper.mapToUserDto(savedUser);
            return savedUser;
        }catch(Exception e){
            System.err.println("Error while adding user: " + e.getMessage());
            // Rejeter une exception personnalisée ou une exception runtime
            throw new RuntimeException("Error while adding user", e);
        }
    }

    @Override
    public User update(Long Id , User user) throws Exception {
            //User UserExisting = UserRepository.findById(user.getId());
            User userExisting = userRepository.findById(user.getId())
                    .orElseThrow(() -> new Exception("User not found with id: " + user.getId()));
            //orElseThrow(()->new TaxesException(ExeceptionMessage.Taxe_UPDATE_FAILED_BY_ID ) );
            BeanUtils.copyProperties(user, userExisting);
            User updateUser= userRepository.save(userExisting);
            return userdtomapper.mapToUser(updateUser);
    }

    @Override
    public void delete(Long Id) {
            /*Optional<User> optionalUser = userRepository.findById(Id);*/
        userRepository.deleteById(Id);

    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }




}
