package vttp2022b2.webapp.day14revision.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vttp2022b2.webapp.day14revision.model.Cart;
import vttp2022b2.webapp.day14revision.model.CartItem;
import vttp2022b2.webapp.day14revision.store.CartRepository;

@Controller
@RequestMapping(path = "/cart")
public class ShoppingCartController {
    private static final Logger logger = LoggerFactory.getLogger(ShoppingCartController.class);

    @Autowired
    ApplicationArguments appArgs;

    @Autowired
    CartRepository repo;

    @PostConstruct
    public void init() {
        // TODO
    }

    /**
     * Show the cart form
     * 
     * @param model
     * @return
     */
    @GetMapping
    public String showAllCart(Model model, @RequestParam String username) {
        repo.setUsername(username);
        repo.setFileRepository(new File(getDataDir(appArgs, "/tmp/data")));
        Cart currCart = repo.load();
        model.addAttribute("cart", currCart);
        return "shoppingcart";
    }

    /**
     * add new cart item to the cart data file.
     * 
     * @param cart
     * @param model
     * @return
     * @throws IOException
     */
    @PostMapping
    public String addCartItem(@ModelAttribute Cart cart, Model model) throws IOException {
        logger.info("add Cart item ");
        List<String> checkDuplicatesOfItem = new ArrayList<>();
        if (cart.getUsername().equals("")) {
            throw new RuntimeException("Username is mandatory");
        }

        if (cart.getItemName().equals("")) {
            throw new RuntimeException("Item name is mandatory");
        }

        if (cart.getPrice().equals("")) {
            throw new RuntimeException("Price is mandatory");
        }
        repo.setUsername(cart.getUsername());
        repo.setFileRepository(new File(getDataDir(appArgs, "/tmp/data")));
        Cart currCart = repo.load();
        CopyOnWriteArrayList<CartItem> currentItemLst = currCart.getCartItems();
        for (CartItem cartItem : currentItemLst) {
            checkDuplicatesOfItem.add(cartItem.getDesc());
        }

        if (currentItemLst.size() > 0) {
            int index = 0;
            for (CartItem cartItem : currentItemLst) {
                if (cartItem.getDesc().equals(cart.getItemName())) {
                    CartItem i = new CartItem();
                    i.setDesc(cart.getItemName());
                    i.setPrice(cart.getPrice());
                    i.setQuantity(cartItem.getQuantity() + 1);
                    currentItemLst.set(index, i);
                    cart.setCartItems(currentItemLst);
                } else {
                    CartItem i = new CartItem();
                    i.setDesc(cart.getItemName());
                    i.setPrice(cart.getPrice());
                    i.setQuantity(1);
                    if (!checkDuplicatesOfItem.contains(cart.getItemName())) {
                        currentItemLst.add(i);
                        checkDuplicatesOfItem.add(cart.getItemName());
                        cart.setCartItems(currentItemLst);
                    }
                }
                logger.info("Current index is: " + (index++));
            }
        } else {
            CartItem i = new CartItem();
            i.setDesc(cart.getItemName());
            i.setPrice(cart.getPrice());
            i.setQuantity(1);
            currentItemLst.add(i);
            checkDuplicatesOfItem.add(cart.getItemName());
            cart.setCartItems(currentItemLst);
        }
        logger.info("new cart item size: " + (cart.getCartItems().size()));
        repo.save(cart);
        Cart newCart = repo.load();
        model.addAttribute("cart", newCart);
        return "shoppingcart";
    }

    /**
     * Update the data file
     * 
     * @param cart
     * @param model
     * @return
     * @throws IOException
     */
    @PostMapping("/update")
    public String updateCartItem(@ModelAttribute Cart cart, Model model) throws IOException {
        logger.info(" update cart");
        repo.setUsername(cart.getUsername());
        repo.setFileRepository(new File(getDataDir(appArgs, "/tmp/data")));
        Cart c = this.repo.load();
        int index = 0;
        for (CartItem item : c.getCartItems()) {
            logger.info(" item.getId() " + item.getId());
            if (item.getId().equals(cart.getEditCartId())) {
                item.setDesc(cart.getItemName());
                item.setPrice(cart.getPrice());
                c.getCartItems().set(index, item);
            }
            index++;
        }
        repo.save(c);
        model.addAttribute("cart", c);
        return "shoppingcart";
    }

    /**
     * Delete cart item from the data file
     * 
     * @param cart
     * @param model
     * @param cartId
     * @param username
     * @return
     */
    @GetMapping("/sortUp/{cartId}")
    public String sortUpCartItem(@ModelAttribute Cart cart, Model model,
            @PathVariable String cartId,
            @RequestParam String username) {
        repo.setUsername(cart.getUsername());
        repo.setFileRepository(new File(getDataDir(appArgs, "/tmp/data")));
        Cart c = this.repo.load();
        int index = 0;
        for (CartItem item : c.getCartItems()) {
            logger.info(" item.getId() " + item.getId());
            if (item.getId().equals(cartId)) {
                CartItem aboveCartItem = c.getCartItems().get(index - 1);
                c.getCartItems().set(index - 1, item);
                c.getCartItems().set(index, aboveCartItem);
            }
            index++;
        }
        repo.save(c);
        model.addAttribute("cart", c);
        return "shoppingcart";
    }

    /**
     * Delete cart item from the data file
     * 
     * @param cart
     * @param model
     * @param cartId
     * @param username
     * @return
     */
    @GetMapping("/delete/{cartId}")
    public String deleteCartItem(@ModelAttribute Cart cart, Model model,
            @PathVariable String cartId,
            @RequestParam String username) {
        repo.setUsername(cart.getUsername());
        repo.setFileRepository(new File(getDataDir(appArgs, "/tmp/data")));
        Cart c = this.repo.load();
        this.repo.delete(cartId, c);
        Cart newCart = repo.load();
        model.addAttribute("cart", newCart);
        return "shoppingcart";
    }

    /**
     * show edit info on the form of the web app
     * 
     * @param cart
     * @param model
     * @param cartId
     * @param username
     * @return
     */
    @GetMapping("/edit/{cartId}")
    public String editCartItem(@ModelAttribute Cart cart, Model model,
            @PathVariable String cartId,
            @RequestParam String username) {
        logger.info("edit cart item : " + cartId);
        logger.info("edit cart item : " + username);
        repo.setUsername(cart.getUsername());
        repo.setFileRepository(new File(getDataDir(appArgs, "/tmp/data")));
        Cart c = this.repo.load();
        Cart editCart = new Cart(username);
        for (CartItem item : c.getCartItems()) {
            logger.info(" item.getId() " + item.getId());
            if (item.getId().equals(cartId)) {
                editCart.setItemName(item.getDesc());
                editCart.setUsername(username);
                editCart.setPrice(item.getPrice());
                editCart.setEditCartId(item.getId());
                editCart.setCartItems(c.getCartItems());
            }
        }
        model.addAttribute("cart", editCart);
        return "shoppingcart";
    }

    /**
     * get the data dir from using nio
     * 
     * @param appArgs
     * @param defaultDataDir
     * @return
     */
    private String getDataDir(ApplicationArguments appArgs, String defaultDataDir) {
        String dataDirResult = "";
        List<String> optValues = null;
        String[] optValuesArr = null;
        Set<String> opsNames = appArgs.getOptionNames();
        String[] optNamesArr = opsNames.toArray(new String[opsNames.size()]);
        if (optNamesArr.length > 0) {
            optValues = appArgs.getOptionValues(optNamesArr[0]);
            optValuesArr = optValues.toArray(new String[optValues.size()]);
            dataDirResult = optValuesArr[0];
        } else {
            dataDirResult = defaultDataDir;
        }

        return dataDirResult;
    }
}
