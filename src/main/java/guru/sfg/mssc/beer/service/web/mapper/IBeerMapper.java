//: guru.sfg.mssc.beer.service.web.mapper.IBeerMapper.java


package guru.sfg.mssc.beer.service.web.mapper;


import guru.sfg.mssc.beer.service.domain.model.Beer;
import guru.sfg.mssc.beer.service.web.model.BeerDto;
import org.mapstruct.Mapper;


@Mapper(uses = {DateTimeMapper.class})
public interface IBeerMapper {

    Beer beerDtoToBeer(BeerDto dto);

    BeerDto beerToBeerDto(Beer beer);

}///:~