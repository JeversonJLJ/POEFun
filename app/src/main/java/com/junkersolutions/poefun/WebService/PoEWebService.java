package com.junkersolutions.poefun.WebService;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.junkersolutions.poefun.Entities.Exchange;
import com.junkersolutions.poefun.Entities.Status;
import com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn.ResultItems;
import com.junkersolutions.poefun.WebService.WebServiceEntitiesReturn.ResultSearch;

import org.json.JSONObject;

import java.util.List;

public class PoEWebService {

    public interface OnGetBulkExchangeSearch {
        void onGetBulkExchangeSearch(ResultSearch resultSearch);
        void onGetBulkExchangeSearchError(Exception e);
    }



    public void getBulkExchangeSearch(final Context context, String league, List<String> have, List<String> want, String statusRequest, final OnGetBulkExchangeSearch onGetBulkExchangeSearch) {
        try {
            Exchange exchange = new Exchange();
            exchange.setHave(have);
            exchange.setWant(want);
            Status status = new Status();
            status.setOption(statusRequest);
            exchange.setStatus(status);

            JSONObject jsonObjectPostRequest = new JSONObject(new Gson().toJson(new PoEWebServiceRequest(exchange)));
            JsonObjectRequest req = new JsonObjectRequest("https://www.pathofexile.com/api/trade/exchange/" + league, jsonObjectPostRequest, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Gson gson = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .create();
                        ResultSearch resultSearch = gson.fromJson(response.toString(), ResultSearch.class);
                        onGetBulkExchangeSearch.onGetBulkExchangeSearch(resultSearch);

                    } catch (Exception e) {
                        onGetBulkExchangeSearch.onGetBulkExchangeSearchError(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    onGetBulkExchangeSearch.onGetBulkExchangeSearchError(e);
                }
            }

            );

            req.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(req);


        } catch (Exception e) {
            onGetBulkExchangeSearch.onGetBulkExchangeSearchError(e);
        }

    }

    public interface OnGetBulkExchangeItem {
        void onGetBulkExchangeItem(ResultItems resultItems);
        void onGetBulkExchangeItemError(Exception e);
    }

    public void getBulkExchangeItem(Context context, List<String> items, String id, final OnGetBulkExchangeItem onGetBulkExchangeItem) {
        try {
            String itemsUrl = "";
            for (String item : items)
                itemsUrl += item + ",";
            if (itemsUrl != null && !items.isEmpty())
                itemsUrl = itemsUrl.substring(0, itemsUrl.length() - 1);

            JsonObjectRequest req = new JsonObjectRequest("https://www.pathofexile.com/api/trade/fetch/" + itemsUrl + "?query="+ id +"&exchange", null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        Gson gson = new GsonBuilder()
                                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                .create();
                        ResultItems result = gson.fromJson(response.toString(), ResultItems.class);
                        onGetBulkExchangeItem.onGetBulkExchangeItem(result);
                    } catch (Exception e) {
                        onGetBulkExchangeItem.onGetBulkExchangeItemError(e);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError e) {
                    onGetBulkExchangeItem.onGetBulkExchangeItemError(e);
                }
            }

            );

            req.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(req);


        } catch (Exception e) {
            onGetBulkExchangeItem.onGetBulkExchangeItemError(e);
        }

    }


    public class PoEWebServiceRequest {
        Exchange exchange;

        public PoEWebServiceRequest(Exchange exchange) {
            this.exchange = exchange;
        }

        public Exchange getExchange() {
            return exchange;
        }

        public void setExchange(Exchange exchange) {
            this.exchange = exchange;
        }
    }
}

