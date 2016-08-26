package io.ipoli.android.shop;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.ipoli.android.R;
import io.ipoli.android.app.ui.IconButton;
import io.ipoli.android.shop.viewmodels.PetViewModel;

/**
 * Created by Venelin Valkov <venelin@curiousily.com>
 * on 8/26/16.
 */
public class ShopPetAdapter extends PagerAdapter {

    private final LayoutInflater layoutInflater;
    private final List<PetViewModel> viewModels;

    public ShopPetAdapter(final Context context, List<PetViewModel> viewModels) {
        this.viewModels = viewModels;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final View view = layoutInflater.inflate(R.layout.shop_pet_item, container, false);
        PetViewModel vm = viewModels.get(position);

        TextView petDescription = (TextView) view.findViewById(R.id.pet_description);
        ImageView petPicture = (ImageView) view.findViewById(R.id.pet_picture);
        ImageView petStatePicture = (ImageView) view.findViewById(R.id.pet_picture_state);
        IconButton petPrice = (IconButton) view.findViewById(R.id.pet_price);

        petDescription.setText(vm.getDescription());
        petPicture.setImageResource(vm.getPicture());
        petStatePicture.setImageResource(vm.getPictureState());
        petPrice.setText(vm.getPrice() + "");

        container.addView(view);
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }
}
