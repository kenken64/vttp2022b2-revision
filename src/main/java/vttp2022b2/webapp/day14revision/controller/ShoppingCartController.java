package vttp2022b2.webapp.day14revision.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @GetMapping
    public String showShoppingCartForm(Model model) {
        Cart c = new Cart("");
        model.addAttribute("cart", c);
        return "shoppingcart";
    }

    /**
     * @param cart
     * @param model
     * @return
     * @throws IOException
     */
    @PostMapping
    public String addCartItem(@ModelAttribute Cart cart, Model model) throws IOException {
        logger.info("add Cart item ");
        if (cart.getUsername().equals("")) {
            throw new IOException("Username is mandatory");
        }
        repo.setUsername(cart.getUsername());
        repo.setFileRepository(new File(getDataDir(appArgs, "/tmp/data")));
        Cart currCart = repo.load();
        CopyOnWriteArrayList<CartItem> currentItemLst = currCart.getCartItems();

        if (currentItemLst.size() > 0) {
            for (CartItem cartItem : currentItemLst) {
                if (cartItem.getDesc().equals(cart.getItemName())) {
                    CartItem i = new CartItem();
                    i.setDesc(cart.getItemName());
                    i.setPrice(cart.getPrice());
                    i.setQuantity(cartItem.getQuantity() + 1);
                    currentItemLst.add(i);
                    cart.setCartItems(currentItemLst);
                } else {
                    CartItem i = new CartItem();
                    i.setDesc(cart.getItemName());
                    i.setPrice(cart.getPrice());
                    i.setQuantity(1);
                    currentItemLst.add(i);
                    cart.setCartItems(currentItemLst);
                }
            }
        } else {
            CartItem i = new CartItem();
            i.setDesc(cart.getItemName());
            i.setPrice(cart.getPrice());
            i.setQuantity(1);
            currentItemLst.add(i);
            cart.setCartItems(currentItemLst);
        }
        repo.save(cart);
        model.addAttribute("cart", cart);
        return "shoppingcart";
    }

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
