package vttp2022b2.webapp.day14revision.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cart {
    private static final Logger logger = LoggerFactory.getLogger(Cart.class);

    private static final String CART_ITEM_DELIM = ",";
    private CopyOnWriteArrayList<CartItem> cartItems = new CopyOnWriteArrayList<>();
    private String username;
    private String itemName;
    private BigDecimal price;

    public Cart(String name) {
        this.username = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setCartItems(CopyOnWriteArrayList<CartItem> contents) {
        this.cartItems = contents;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getUsername() {
        return username;
    }

    public void add(CartItem item) {
        for (CartItem inCart : cartItems)
            if (inCart.getDesc().equals(item.getDesc()))
                return;
        cartItems.add(item);
    }

    public CartItem delete(int index) {
        if (index < cartItems.size())
            return cartItems.remove(index);
        return new CartItem();
    }

    public void load(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String itemStr;
        CartItem item;
        while ((itemStr = br.readLine()) != null) {
            logger.info(itemStr);
            String cartItemWifDelim = (String) itemStr;
            if (cartItemWifDelim != null) {
                String[] cartItemArr = cartItemWifDelim.split(CART_ITEM_DELIM);
                item = new CartItem();
                logger.info(cartItemArr[0]);
                logger.info(cartItemArr[1]);
                logger.info(cartItemArr[2]);
                item.setDesc(cartItemArr[0]);
                item.setQuantity(Integer.parseInt(cartItemArr[1]));
                item.setPrice(BigDecimal.valueOf(Long.parseLong(cartItemArr[2])));
                item.setId(cartItemArr[3]);
                cartItems.add(item);
            }

        }

        br.close();
        isr.close();
    }

    public void save(OutputStream os) throws IOException {
        OutputStreamWriter ows = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(ows);
        for (CartItem item : cartItems)
            bw.write(item.getDesc() + "\n" + CART_ITEM_DELIM + item.getQuantity()
                    + CART_ITEM_DELIM + item.getPrice());

        ows.flush();
        bw.flush();
        bw.close();
        ows.close();
    }

    public CopyOnWriteArrayList<CartItem> getCartItems() {
        return cartItems;
    }

}
