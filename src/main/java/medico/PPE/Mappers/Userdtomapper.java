package medico.PPE.Mappers;


import medico.PPE.Models.User;
import medico.PPE.dtos.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface Userdtomapper {
    Userdtomapper userdtomapper = Mappers.getMapper(Userdtomapper.class);
    @Mapping(target = "username",ignore = true)
    UserDto mapToUserDto(User user);
    @Mapping(target = "email",ignore = true)
    User mapToUser(User userDto);
}
