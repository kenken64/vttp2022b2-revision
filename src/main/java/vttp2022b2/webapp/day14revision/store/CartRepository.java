package vttp2022b2.webapp.day14revision.store;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import vttp2022b2.webapp.day14revision.model.Cart;
import vttp2022b2.webapp.day14revision.model.CartItem;
import static vttp2022b2.webapp.day14revision.util.IOUtil.*;

@Component(value = "cartRepo")
public class CartRepository {
    private static final Logger logger = LoggerFactory.getLogger(CartRepository.class);

    private CopyOnWriteArrayList<CartItem> contents = new CopyOnWriteArrayList<>();
    private String username;
    private File fileRepository;

    public CartRepository() {

    }

    public CartRepository(String name, String dataDir) {
        this.username = name;
        this.fileRepository = new File(dataDir);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public File getFileRepository() {
        return fileRepository;
    }

    public void setFileRepository(File fileRepository) {
        this.fileRepository = fileRepository;
    }

    public String getUsername() {
        return username;
    }

    public void add(CartItem item) {
        for (CartItem inCart : contents)
            if (inCart.getDesc().equals(item.getDesc()))
                return;
        contents.add(item);
    }

    public void delete(String cartId, Cart c) {
        int index = 0;
        logger.info(" cartid " + cartId);
        logger.info(" contents " + c.getCartItems().size());
        CopyOnWriteArrayList<CartItem> contentsDel = c.getCartItems();
        for (CartItem item : contentsDel) {
            logger.info(" item.getId() " + item.getId());
            if (item.getId().equals(cartId)) {
                logger.info("Delete cartid " + cartId);
                contentsDel.remove(index);
                break;
            }
            index++;
        }
        c.setCartItems(contentsDel);
        this.save(c, false);
    }

    public void load(InputStream is) throws IOException {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String itemStr;
        while ((itemStr = br.readLine()) != null) {
            if (itemStr != null) {
                logger.info(itemStr);
                String[] itemStrArr = itemStr.split(",");
                CartItem i = new CartItem();
                i.setDesc(itemStrArr[0]);
                i.setQuantity(Integer.parseInt(itemStrArr[1]));
                i.setPrice(new BigDecimal(itemStrArr[2]));
                i.setId(itemStrArr[3]);
                contents.add(i);
            }
        }

        br.close();
        isr.close();
    }

    public synchronized Cart load() {
        String cartName = this.username + ".cart";
        Cart cart = new Cart(this.username);
        if (fileRepository.listFiles() == null) {
            createDir(fileRepository.getPath());
        }
        for (File cartFile : fileRepository.listFiles())
            if (cartFile.getName().equals(cartName)) {
                try {
                    InputStream is = new FileInputStream(cartFile);
                    cart.load(is);
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return cart;
    }

    public void save(OutputStream os, CopyOnWriteArrayList<CartItem> items) throws IOException {
        logger.info("save to file");
        OutputStreamWriter ows = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(ows);
        for (CartItem item : items) {
            logger.info(item.getDesc());
            logger.info("" + item.getQuantity());
            logger.info("" + item.getPrice());
            bw.write(item.getDesc() + "," + item.getQuantity() + ","
                    + item.getPrice() + "," + item.getId() + "\n");
        }
        ows.flush();
        bw.flush();
        bw.close();
        ows.close();
    }

    public synchronized void save(Cart cart) {
        this.save(cart, false);
    }

    public synchronized void save(Cart cart, boolean isDel) {
        String cartName = cart.getUsername() + ".cart";
        String saveLocation = fileRepository.getPath() + File.separator + cartName;
        File saveFile = new File(saveLocation);
        OutputStream os = null;
        try {
            if (!saveFile.exists()) {
                Path path = Paths.get(fileRepository.getPath());
                Files.createDirectories(path);
                saveFile.createNewFile();
            }

            if (isDel) {
                os = new FileOutputStream(saveLocation, true);
            } else {
                os = new FileOutputStream(saveLocation, false);
            }

            this.save(os, cart.getCartItems());
            os.flush();
            os.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public CopyOnWriteArrayList<CartItem> getContents() {
        return contents;
    }
}
