//: guru.sfg.mssc.beer.service.web.controller.BeerControllerIT.java


package guru.sfg.mssc.beer.service.web.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import guru.sfg.mssc.beer.service.domain.model.Beer;
import guru.sfg.mssc.beer.service.domain.repositories.IBeerRepository;
import guru.sfg.mssc.beer.service.web.model.BeerDto;
import guru.sfg.mssc.beer.service.web.model.BeerStyleEnum;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureRestDocs
@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "guru.sfg.mssc.beer.service.web.mapper")
@ExtendWith(RestDocumentationExtension.class)
@DisplayName("Beer's MVC Controller Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BeerControllerIT {

    static final String REQUEST_MAPPING = "/api/v1/beer";

    private String uuid;
    private String name;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    ApplicationContext context;

    /*
     * @MockBean is used to add mock objects to the Spring application context
     *
     * The mock will replace any existing bean of the same type in the
     * application context
     *
     * If no bean of the same type is defined, a new one will be added
     *
     * This annotation is useful in integration tests where a particular bean
     *   – for example, an external service – needs to be mocked
     *
     */
    @MockBean
    private IBeerRepository beerRepository;

    private UnaryOperator<String> uriFunc = uuid ->
            String.format("%s/%s", REQUEST_MAPPING, uuid);

    private BeerDto dto;

    @BeforeEach
    void setUp() {
        this.uuid = UUID.randomUUID().toString();
        this.name = RandomStringUtils.randomAlphabetic(10);
        this.dto = BeerDto.builder()
                .beerName(this.name)
                .beerStyle(BeerStyleEnum.STOUT)
                .upc(Long.toString(System.currentTimeMillis()))
                .price(BigDecimal.valueOf(12.00))
                .build();
    }

    @Test
    void test_Given_An_Id_When_Get_Beer_By_Id_Then_Get_Http_Status_200()
            throws Exception {

        // Given
        given(this.beerRepository.findById(UUID.fromString(this.uuid)))
                .willReturn(Optional.of(Beer.builder().build()));

        // When & Then
        this.mockMvc.perform(get(uriFunc.apply(this.uuid))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void test_Given_An_Beer_Obj_When_Save_Then_Get_Beer_Created()
            throws Exception {

        // Given
        String beerDtoJson = this.objectMapper.writeValueAsString(this.dto);

        // When
        this.mockMvc.perform(
                post(REQUEST_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoJson))
                .andExpect(status().isCreated());

        // Then
        then(this.beerRepository).should(times(1))
                .save(any(Beer.class));
    }

    @Test
    void test_Given_An_Beer_Obj_With_UUID_When_Update_Then_Get_Beer_Updated()
            throws Exception {

        // Given
        Beer beer = Beer.builder().id(UUID.fromString(this.uuid)).build();
        given(this.beerRepository.findById(UUID.fromString(this.uuid)))
                .willReturn(Optional.of(beer));

        String beerDtoJson = this.objectMapper.writeValueAsString(this.dto);

        // When
        this.mockMvc
                .perform(put(uriFunc.apply(this.uuid))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoJson))
                .andExpect(status().isNoContent());

        // Then
        then(this.beerRepository).should(times(1))
                .save(isA(Beer.class));
    }

    @Test
    void test_Given_An_Invalid_Beer_When_Update_Then_Get_List_Of_Error_Messages()
            throws Exception {

        // Given
        BeerDto beerDto = BeerDto.builder()
                .id(UUID.randomUUID())
                .build();
        String beerDtoJson = this.objectMapper.writeValueAsString(beerDto);

        // When
        this.mockMvc.perform(put(uriFunc.apply(this.uuid))
                .contentType(MediaType.APPLICATION_JSON)
                .content(beerDtoJson))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        containsString("id must be null")))
                .andExpect(content().string(
                        containsString("beerName must not be blank")))
                .andExpect(content().string(
                        containsString("beerStyle must not be null")))
                .andExpect(content().string(
                        containsString("upc must not be blank")));
    }

}///:~