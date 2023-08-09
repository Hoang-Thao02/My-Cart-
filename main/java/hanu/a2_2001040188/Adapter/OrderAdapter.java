package hanu.a2_2001040188.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

import hanu.a2_2001040188.CartActivity;
import hanu.a2_2001040188.Data.OrderHelper;
import hanu.a2_2001040188.R;
import hanu.a2_2001040188.models.Constants.Constants;
import hanu.a2_2001040188.models.Order.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    List<Order> orders = new ArrayList<>();
    private Context mContext;
    /**
     * Lớp nắm giữ cấu trúc view
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final View cartitem;
        public ImageView OrderImageView;
        public TextView orderPrice;
        public TextView orderSumPrice;
        public ImageButton btnAdd;
        public ImageButton btnSub;
        public TextView orderTitle;
        public TextView orderQuantity;

        public ViewHolder(View cartItem) {
            super(cartItem);
            cartitem = cartItem;
            orderSumPrice = cartItem.findViewById(R.id.txtOrderSumPrice);
            orderPrice = cartItem.findViewById(R.id.txtOrderPrice);
            orderTitle = cartItem.findViewById(R.id.txtOrderTitle);
            btnSub = cartItem.findViewById(R.id.btnOrderSub);
            btnAdd = cartItem.findViewById(R.id.btnOrderAdd);
            orderQuantity= cartItem.findViewById(R.id.txtOrderQuantity);
            OrderImageView = cartItem.findViewById(R.id.OrderImageView);
        }
    }
    public OrderAdapter(List _orders, Context mContext) {
        this.orders = _orders;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View orderView = inflater.inflate(R.layout.cart_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(orderView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Log.i("OrderAdapter",orders.get(position).toString());
        Order order = orders.get(position);

//        !TODO: IMPLEMENT THIS orders SYSTEM

        holder.orderTitle.setText(order.getName());
        NumberFormat formatter = new DecimalFormat("#,###");
        String formattedNumber = formatter.format(order.getPrice());
        holder.orderPrice.setText("đ̳ "+formattedNumber);
        holder.orderQuantity.setText(order.getQuantity().toString());
        formattedNumber=formatter.format((order.getPrice()*order.getQuantity()));
        holder.orderSumPrice.setText("đ̳ "+formattedNumber);
        //?Task Chung
        Handler handler = HandlerCompat.createAsync(Looper.getMainLooper());
        //Showing Image
        Constants.executor.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = downloadImage(order.getThumbnail());
                if (bitmap !=null ) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.OrderImageView.setImageBitmap(bitmap);
                        }
                    });
                }
            }
        });

        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increaseOrderItem(mContext,position);
                TextView sum =((CartActivity) mContext).findViewById(R.id.txtTotalPrice);
                Double Total_Price =(Double) 0.0;
                for (int i=0;i<orders.size();i++) {
                    Total_Price=Total_Price + orders.get(i).getPrice()*orders.get(i).getQuantity();
                }
                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(Total_Price);
                sum.setText("đ̳ "+formattedNumber);
                holder.btnAdd.animate().setDuration(500).rotationBy(360f).start();
            }
        });
        holder.btnSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                decreaseOrderItem(mContext,position);
                TextView sum =(( CartActivity) mContext).findViewById(R.id.txtTotalPrice);
                Double Total_Price =(Double) 0.0;
                for (int i=0;i<orders.size();i++) {
                    Total_Price=Total_Price + orders.get(i).getPrice()*orders.get(i).getQuantity();
                }
                NumberFormat formatter = new DecimalFormat("#,###");
                String formattedNumber = formatter.format(Total_Price);
                sum.setText("đ̳ "+formattedNumber);
                holder.btnSub.animate().setDuration(500).rotationBy(360f).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }
    //!Method to Download Image
    private boolean decreaseOrderItem(Context mContext, int position) {
        OrderHelper orderHelper = new OrderHelper(mContext);
        SQLiteDatabase db = orderHelper.getWritableDatabase();
        Log.i("Hello",orders.get(position).getQuantity().toString());
        if(orders.get(position).getQuantity()==1) {
            //quantity =1 -> xoa
            Log.i("position",String.valueOf(position));
            String strDelete ="DELETE from orders WHERE product_id= "+orders.get(position).getId();
            Log.i("Hello","This 1");
            db.execSQL(strDelete);
            orders.remove(position);
            Log.i("Array size",String.valueOf(getItemCount()));
            notifyItemRangeChanged(position,getItemCount());
            notifyItemRemoved(position);
            db.close();
        } else {
            Log.i("position",String.valueOf(position));
            orders.get(position).setQuantity(orders.get(position).getQuantity()-1);
            String strUpdate = "UPDATE orders SET quantity = quantity -1 WHERE product_id = "+ orders.get(position).getId();
            notifyItemChanged(position);
            db.execSQL(strUpdate);
            Log.i("Array size",String.valueOf(getItemCount()));
            db.close();
        }
        return true;
    }
    private boolean increaseOrderItem( Context mContext,int position) {
        System.out.print("Run");
        OrderHelper orderHelper = new OrderHelper(mContext);
        SQLiteDatabase db = orderHelper.getWritableDatabase();
        String strUpdate = "UPDATE orders SET quantity = quantity +1 WHERE product_id = "+ orders.get(position).getId();
        orders.get(position).setQuantity(orders.get(position).getQuantity()+1);
        notifyItemChanged(position);
        db.execSQL(strUpdate);
        db.close();
        return true;
    }

    private Bitmap downloadImage(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); connection.connect();
            InputStream is = connection.getInputStream(); Bitmap bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (MalformedURLException e) { e.printStackTrace();
        } catch (IOException e) { e.printStackTrace();
        }
        return null;
    }

}
