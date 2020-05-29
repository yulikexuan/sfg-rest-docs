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
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@AutoConfigureRestDocs(uriScheme = "https", uriHost = "cloudlab2020.yulikexuan.com", uriPort = 8081)
@WebMvcTest(BeerController.class)
@ComponentScan(basePackages = "guru.sfg.mssc.beer.service.web.mapper")
@ExtendWith(RestDocumentationExtension.class)
@DisplayName("Beer's MVC Controller Test - ")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BeerControllerIT {

    static final String REQUEST_MAPPING = "/api/v1/beer";
    static final String PATH_PARAM_BEER_ID = "beerId";

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

    private Supplier<String> uriSupplier = () ->
            String.format("%s/{%s}", REQUEST_MAPPING, PATH_PARAM_BEER_ID);

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
        this.mockMvc.perform(get(uriSupplier.get(), this.uuid)
                .param("isCold", "true")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("v1/beer-get",
                        pathParameters(parameterWithName(PATH_PARAM_BEER_ID)
                                .description("UUID of desired beer to get.")),
                        requestParameters(parameterWithName("isCold")
                                .description("Is beer colde query parameter")),
                        responseFields(
                                fieldWithPath("id").description("Id of Beer").type(UUID.class),
                                fieldWithPath("version").description("Version number").type(Integer.class),
                                fieldWithPath("createdDate").description("Creating time").type(OffsetDateTime.class),
                                fieldWithPath("lastModifiedDate").description("Recent modified time").type(OffsetDateTime.class),
                                fieldWithPath("beerName").description("The name of beer").type(String.class),
                                fieldWithPath("beerStyle").description("The style of beer").type(BeerStyleEnum.class),
                                fieldWithPath("upc").description("UPC of Beer").type(String.class),
                                fieldWithPath("price").description("The price of beer").type(BigDecimal.class),
                                fieldWithPath("quantityOnHand").description("Quantity on Hand").type(Integer.class))));
    }

    @Test
    void test_Given_An_Beer_Obj_When_Save_Then_Get_Beer_Created()
            throws Exception {

        // Given
        String beerDtoJson = this.objectMapper.writeValueAsString(this.dto);

        ConstrainedFields constraintFields = new ConstrainedFields(BeerDto.class);

        // When
        this.mockMvc.perform(post(REQUEST_MAPPING)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(beerDtoJson))
                .andExpect(status().isCreated())
                .andDo(document("v1/beer-new", requestFields(
                        constraintFields.withPath("id").ignored(),
                        constraintFields.withPath("version").ignored(),
                        constraintFields.withPath("createdDate").ignored(),
                        constraintFields.withPath("lastModifiedDate").ignored(),
                        constraintFields.withPath("beerName").description("The name of beer"),
                        constraintFields.withPath("beerStyle").description("The style of beer"),
                        constraintFields.withPath("upc").description("Beer UPC"),
                        constraintFields.withPath("price").description("The price of beer"),
                        constraintFields.withPath("quantityOnHand").ignored())));

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
        this.mockMvc.perform(
                put(uriSupplier.get(), this.uuid)
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
        this.mockMvc.perform(put(uriSupplier.get(), this.uuid)
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

    private static class ConstrainedFields {

        private final ConstraintDescriptions constraintDesc;

        ConstrainedFields(Class<?> input) {
            this.constraintDesc = new ConstraintDescriptions(input);
        }

        private FieldDescriptor withPath(String path) {

            List<String> descs = this.constraintDesc.descriptionsForProperty(path);
            String allDescs = StringUtils.collectionToDelimitedString(descs, ". ");

            return fieldWithPath(path).attributes(
                    key("constraints").value(allDescs));
        }
    }

}///:~