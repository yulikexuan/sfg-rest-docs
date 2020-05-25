//: guru.sfg.mssc.beer.service.bootstrap.BeerLoader.java


package guru.sfg.mssc.beer.service.bootstrap;


import guru.sfg.mssc.beer.service.domain.model.Beer;
import guru.sfg.mssc.beer.service.domain.repositories.IBeerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Slf4j
@Component
public class BeerLoader implements CommandLineRunner {

    public static final long BEER_1_UPC = 631234200036L;
    public static final long BEER_2_UPC = 631234300019L;
    public static final long BEER_3_UPC = 83783375213L;

    private final IBeerRepository beerRepository;

    @Autowired
    public BeerLoader(IBeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (this.beerRepository.count() == 0) {
            this.loadBeerObjects();
        }
        log.info(">>>>>>> Loaded Beers: {}", this.beerRepository.count());
    }

    private void loadBeerObjects() {

        Beer b1 = Beer.builder()
                .beerName("Mango Bobs")
                .beerStyle("IPA")
                .minOnHand(12)
                .quantityToBrew(200)
                .price(new BigDecimal("12.95"))
                .upc(BEER_1_UPC)
                .build();

        this.beerRepository.save(b1);

        Beer b2 = Beer.builder()
                .beerName("Galaxy Cat")
                .beerStyle("PALE_ALE")
                .minOnHand(12)
                .quantityToBrew(200)
                .price(new BigDecimal("12.95"))
                .upc(BEER_2_UPC)
                .build();

        this.beerRepository.save(b2);

        Beer b3 = Beer.builder()
                .beerName("Pinball Porter")
                .beerStyle("PALE_ALE")
                .minOnHand(12)
                .quantityToBrew(200)
                .price(new BigDecimal("12.95"))
                .upc(BEER_3_UPC)
                .build();

        this.beerRepository.save(b3);
    }

}///:~