/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.services;

import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.events.ActionListener;
import com.mycompany.entites.Category;
import com.mycompany.entites.Product;
import com.mycompany.utils.Statics;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author HP
 */
public class ServiceProduct {

    Boolean resultOK;
    public static ServiceProduct instance = null;

    public static boolean resultOk = true;

    private ConnectionRequest req;

    public static ServiceProduct getInstance() {
        if (instance == null) {
            instance = new ServiceProduct();
        }
        return instance;

    }

    public ServiceProduct() {
        req = new ConnectionRequest();
    }

    public void addProduct(Product p) {

        String url = Statics.BASE_URL + "products/product/addproductmobile/new/" + p.getName()
                + "&price=" + p.getPrice()
                + "&qty=" + p.getQty()
                + "&description=" + p.getDesc();

        req.setUrl(url);
        req.addResponseListener((e) -> {

            String str = new String(req.getResponseData());
            System.out.println("data == " + str);
        });

        NetworkManager.getInstance().addToQueueAndWait(req);

    }

    public ArrayList<Product> getProduct() {

        ArrayList<Product> result = new ArrayList<>();
        String url = Statics.BASE_URL + "products/product/getproductmobile";
        req.setUrl(url);
        req.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {

                JSONParser jsonp;
                jsonp = new JSONParser();

                try {
                    Map<String, Object> mapProducts = jsonp.parseJSON(new CharArrayReader(new String(req.getResponseData()).toCharArray()));
                    List<Map<String, Object>> ListOfMaps = (List<Map<String, Object>>) mapProducts.get("root");
                    for (Map<String, Object> obj : ListOfMaps) {

                        Product p = new Product();

                        float id = Float.parseFloat(obj.get("id").toString());
                        String name = obj.get("name").toString();
                        float price = Float.parseFloat(obj.get("price").toString());
                        float qty = Float.parseFloat(obj.get("qty").toString());
                        String desc = obj.get("description").toString();
                        String image = obj.get("image").toString();
                        Category idCat = (Category) obj.get("idCat");

                        p.setId((int) id);
                        p.setName(name);
                        p.setPrice((int) price);
                        p.setQty((int) qty);
                        p.setDesc(desc);
                        p.setImage(image);
                        p.setCat(idCat);

                        result.add(p);
                        System.out.println(p.getId() + " " + p.getName() + " " + p.getPrice() + " " + p.getQty() + " " + p.getDesc() + " " + p.getImage());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });

        NetworkManager.getInstance().addToQueueAndWait(req);
        return result;

    }

    public boolean updateProduct(Product p) {
        String url = Statics.BASE_URL + "products/product/updateproductmobile/" + p.getId()
                + "&name=" + p.getName()
                + "&price=" + p.getPrice()
                + "&qty=" + p.getQty()
                + "&description=" + p.getDesc();
        req.setUrl(url);

        req.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {
                resultOk = req.getResponseCode() == 200;
                req.removeResponseListener(this);
            }
        });

        NetworkManager.getInstance().addToQueueAndWait(req);
        return resultOk;

    }

    public boolean deleteProduct(int id) {
        String url = Statics.BASE_URL + "products/product/deleteproductmobile/" + id;

        req.setUrl(url);

        req.addResponseListener(new ActionListener<NetworkEvent>() {
            @Override
            public void actionPerformed(NetworkEvent evt) {

                req.removeResponseCodeListener(this);
            }
        });

        NetworkManager.getInstance().addToQueueAndWait(req);
        return resultOk;
    }

}
