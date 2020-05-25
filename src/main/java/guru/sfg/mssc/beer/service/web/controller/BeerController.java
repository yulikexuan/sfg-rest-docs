//: guru.sfg.mssc.beer.service.web.controller.BeerController.java


package guru.sfg.mssc.beer.service.web.controller;


import guru.sfg.mssc.beer.service.domain.model.Beer;
import guru.sfg.mssc.beer.service.domain.repositories.IBeerRepository;
import guru.sfg.mssc.beer.service.web.mapper.IBeerMapper;
import guru.sfg.mssc.beer.service.web.model.BeerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {

    private final IBeerMapper beerMapper;
    private final IBeerRepository beerRepository;

    @Autowired
    public BeerController(IBeerMapper beerMapper, IBeerRepository beerRepository) {
        this.beerMapper = beerMapper;
        this.beerRepository = beerRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable("id") UUID id) {
        Beer beer = this.beerRepository.findById(id).orElseThrow();
        BeerDto dto = this.beerMapper.beerToBeerDto(beer);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity saveNewBeer(@Valid @RequestBody BeerDto beerDto) {
        Beer beer = this.beerMapper.beerDtoToBeer(beerDto);
        this.beerRepository.save(beer);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity updateBeerById(
            @PathVariable("id") UUID id, @Valid @RequestBody BeerDto beerDto) {

        this.beerRepository.findById(id)
                .ifPresent(beer -> this.updateBeer(beer, beerDto));

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private void updateBeer(Beer beer, BeerDto beerDto) {
        beer.setBeerName(beerDto.getBeerName());
        beer.setBeerStyle(beerDto.getBeerStyle().name());
        beer.setPrice(beerDto.getPrice());
        beer.setUpc(Long.valueOf(beerDto.getUpc()));
        this.beerRepository.save(beer);
    }

}///:~