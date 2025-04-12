package sys.diag.car.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import com.squareup.picasso.Picasso;

import sys.diag.car.R;
import sys.diag.car.models.Car;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {

    private List<Car> cardList;
    private OnItemClickListener listener;
    private final String NO_PREDICTED="Не диагностирован";
    public CardAdapter(List<Car> cardList) {
        this.cardList = cardList;
    }
    public interface OnItemClickListener {
        void onItemClick(Car car);

    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
    public void setCardList(List<Car> newCardList) {
        this.cardList = newCardList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new CardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        holder.tvMarkCar.setText(cardList.get(position).getMarkCar());
        holder.tvYearCar.setText(cardList.get(position).getYearRelease());
        holder.tvIssueBroken.setText(cardList.get(position).getIssueBroken()==null?NO_PREDICTED:cardList.get(position).getIssueBroken());
        String imagePath =cardList.get(position).getImageUri();
        if( imagePath!=null &&!imagePath.equals("No_Data")) {

            Log.e("LOAD_IMAGE",imagePath);

            Picasso.get()
                    .load( "file://"+imagePath) // Здесь вызывайте метод, который возвращает URL изображения
                    .placeholder(R.drawable.img_place_holder) // Заглушка, показываемая во время загрузки изображения
                    .error(R.drawable.img_error) // Заглушка, показываемая в случае ошибки загрузки
                    .into(holder.ivCar);
        }
        else {
            Picasso.get()
                    .load(R.drawable.img_place_holder)
                    .into(holder.ivCar);
        }
        holder.itemView.setOnClickListener(view -> {
            if (listener != null) {
                listener.onItemClick(cardList.get(position));
            }
            Log.d( "id by car in onBindViewHolder: ",String.valueOf(cardList.get(position).getId()));
        });
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvMarkCar,tvYearCar,tvIssueBroken;
        ImageView ivCar;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMarkCar = itemView.findViewById(R.id.tvMarkCarRes);
            tvYearCar = itemView.findViewById(R.id.tvYearReleaseRes);
            tvIssueBroken = itemView.findViewById(R.id.tvIssueBrokenRes);
            ivCar = itemView.findViewById(R.id.ivCar);

        }
    }
}
