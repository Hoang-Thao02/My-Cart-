package hanu.a2_2001040188.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.HandlerCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.LogRecord;

import hanu.a2_2001040188.Data.OrderHelper;
import hanu.a2_2001040188.R;
import hanu.a2_2001040188.models.Constants.Constants;
import hanu.a2_2001040188.models.Order.Order;
import hanu.a2_2001040188.models.Product.Product;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> implements Filterable {
    //save product
    List<Product> products = new ArrayList<>();
    List<Product> fullProducts = new ArrayList<>();
    List<Order> orders = new ArrayList<>();
    // Lưu Context để dễ dàng truy cập
    private Context mContext;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View itemview;
        public TextView productTitle;
        public ImageView productImageView;
        public ImageButton btnAdd;
        public TextView productPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            itemview = itemView;
            productImageView = itemView.findViewById(R.id.ProductImageView);
            productPrice = itemView.findViewById(R.id.txtPrice);
            productTitle = itemView.findViewById(R.id.txtTitle);
            int pos = itemview.getId();
            btnAdd = itemView.findViewById(R.id.btnCart);
        }
    }
    public ProductAdapter(List _products, Context mContext) {
        fullProducts = new ArrayList<>(_products);
        this.products = _products;
        this.mContext = mContext;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product product = products.get(position);
        Log.i("AAA", products.get(position).toString());
        NumberFormat formatter = new DecimalFormat("#,###");
        holder.productTitle.setText(product.getName());
        String formattedNumber = formatter.format(product.getPrice());
        holder.productPrice.setText("đ̳ " + formattedNumber);
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToCart(product, mContext);
                holder.btnAdd.animate().setDuration(500).rotationBy(360f).start();
                Toast.makeText(mContext, "Product added", Toast.LENGTH_SHORT).show();
            }
        });
        //?Task Chung
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        //Showing Image
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(product.getThumbnail());
                if (bitmap != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.productImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View productView = inflater.inflate(R.layout.product_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(productView);
        return viewHolder;
    }
    @Override
    public android.widget.Filter getFilter() {
        return (android.widget.Filter) searchFilter;
    }
    @Override
    public int getItemCount() {
        return products.size();
    }
    private Filter searchFilter = new Filter() {

        protected void publishResults(CharSequence constraint, FilterResults results) {
            products.clear();
            products.addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public boolean isLoggable(LogRecord logRecord) {
            return false;
        }

        private FilterResults performFiltering(CharSequence constraint) {
            List<Product> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(fullProducts);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Product product : fullProducts) {
                    if (product.getName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(product);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }
    };

    //!TODO:Additem to cart
    private boolean addProductToCart(Product product, Context mContext) {
        int pos = -1;
        boolean isAdded = false;
        OrderHelper orderHelper = new OrderHelper(mContext);
        SQLiteDatabase db = orderHelper.getWritableDatabase();
        // manipulate db
        String Query = "Select * from orders where product_id = " + product.getId();
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
        } else {
            isAdded = true;
            cursor.close();
        }

        if (!isAdded) {
            Order order = new Order(product.getId(), product.getThumbnail(), product.getName(), product.getPrice(), 1);
            orders.add(order);

            String sql = "INSERT INTO orders(name,image, price, quantity,product_id) VALUES (?,?, ?, ?, ?)";
            SQLiteStatement statement = db.compileStatement(sql);
            // bind params
            statement.bindString(1, product.getName());
            statement.bindString(2, product.getThumbnail());
            statement.bindDouble(3, product.getPrice());
            statement.bindLong(4, 1);
            statement.bindLong(5, product.getId());

            // run query
            long id = statement.executeInsert(); // auto generated id
            // close connection
            return id > 0;
        } else {
            String strUpdate = "UPDATE orders SET quantity = quantity +1 WHERE product_id = " + product.getId();
            db.execSQL(strUpdate);
        }
        db.close();
        // create statement
        return true;
    }
    private Bitmap downloadImage(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class FilterResults {
        public List<Product> values;
    }
}
