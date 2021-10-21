package com.amr.project.webapp.rest_controller;


import com.amr.project.converter.AddressMapper;
import com.amr.project.converter.ImageMapper;
import com.amr.project.converter.ItemMapper;
import com.amr.project.converter.ShopMapper;
import com.amr.project.model.dto.ImageDto;
import com.amr.project.model.dto.ItemDto;
import com.amr.project.model.dto.ShopDto;
import com.amr.project.model.entity.Country;
import com.amr.project.model.entity.Image;
import com.amr.project.model.entity.Shop;
import com.amr.project.service.abstracts.*;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/shop")
public class ShopRestController {
    private ShopService shopService;
    private final ShopMapper shopMapper;
    private final CountryService countryService;
    private final UserService userService;
    private final ImageService imageService;
    private final ImageMapper imageMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Autowired
    public ShopRestController(ShopService shopService, ShopMapper shopMapper, CountryService countryService, UserService userService,
                              ImageService imageService, ImageMapper imageMapper, ItemService itemService, ItemMapper itemMapper) {
        this.shopService = shopService;
        this.shopMapper = shopMapper;
        this.countryService = countryService;
        this.userService = userService;
        this.imageService = imageService;
        this.imageMapper = imageMapper;
        this.itemService = itemService;
        this.itemMapper = itemMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopDto> getShop(@PathVariable("id") Long id) {
        return new ResponseEntity<>(shopMapper.shopToShopDto(shopService.getByKey(id)), HttpStatus.OK);
    }

    @GetMapping("/{id}/soldItems")
    public ResponseEntity<List<ItemDto>> getShopSoldItems(@PathVariable("id") Long id) {
        return new ResponseEntity<>(itemMapper.toItemsDto(itemService.getSoldItemsByShopId(id)), HttpStatus.OK);
    }

    @PutMapping("/save")
    public ResponseEntity<ShopDto> saveItem(@RequestBody ShopDto shopDto) {

        Shop shop = shopMapper.shopDtoToShop(shopDto);

        Image image = Image.builder()
                .url(shop.getLogo().getUrl())
                .picture(shop.getLogo().getPicture())
                .isMain(true)
                .build();


        Shop oldShop = shopService.getByKey(shop.getId());
        shop.setLocation(countryService.getByName(shopDto.getLocation()));
        shop.setUser(userService.findByUsername(shopDto.getUsername()).get());
        shop.setItems(oldShop.getItems());
        shop.setDiscounts(oldShop.getDiscounts());
        shop.setReviews(oldShop.getReviews());
        shop.setRating(oldShop.getRating());
        shop.setPretendentToBeDeleted(oldShop.isPretendentToBeDeleted());
        shop.setLogo(image);
        shopService.update(shop);

        return new ResponseEntity<ShopDto>(shopMapper.shopToShopDto(shop), HttpStatus.OK);
    }


    @PostMapping("/add")
    public ResponseEntity<Shop> addItem(@RequestBody ShopDto shopDto, Authentication authentication) {
        Shop shop = shopService.addShop(shopDto, userService.findByUsername(authentication.getName()).get());
        return new ResponseEntity<>(shop, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        shopService.deleteByKeyCascadeEnable(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
