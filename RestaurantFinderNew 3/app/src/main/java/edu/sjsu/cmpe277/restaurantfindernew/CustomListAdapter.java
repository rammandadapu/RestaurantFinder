package edu.sjsu.cmpe277.restaurantfindernew;

/**
 * Created by divya.chittimalla on 3/20/16.
 */

import android.media.Image;
import android.widget.BaseAdapter;
import java.util.ArrayList;
import java.util.HashMap;

import android.view.LayoutInflater;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.graphics.drawable.Drawable;

import android.widget.Filter;
import android.widget.Filterable;


public class CustomListAdapter extends BaseAdapter implements Filterable {
    private ArrayList listData;
    private ArrayList filteredData;
    private Context context;
    private LayoutInflater layoutInflater;

    ValueFilter valueFilter;

    private static final String TAG_RES_NAME = "name";
    private static final String TAG_RES_LOCATION = "location";
    private static final String TAG_RES_CITY = "city";
    private static final String TAG_RES_ADDRESS = "display_address";
    private static final String TAG_RES_BUSINESSIMAGE = "image_url";
    private static final String TAG_RES_RATINGIMAGE = "rating_img_url";

    public CustomListAdapter(Context context, ArrayList listData) {
        this.listData = listData;
        filteredData = listData;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public ArrayList getFilteredData() {
        return listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.restaurant_item, null);
            holder = new ViewHolder();
            holder.businessName = (TextView) convertView.findViewById(R.id.name_list);
            holder.businessAddress = (TextView) convertView.findViewById(R.id.address_list);
            holder.businessImage = (ImageView) convertView.findViewById(R.id.business_image_list);
            holder.businessRating = (ImageView) convertView.findViewById(R.id.rating_list);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        HashMap<String, String> map = (HashMap<String, String>) listData.get(position);
        holder.businessName.setText(map.get(TAG_RES_NAME));
        holder.businessAddress.setText(map.get(TAG_RES_ADDRESS));
        if (holder.businessImage != null) {
            //new ImageDownloaderTask(holder.businessImage).execute(map.get(TAG_RES_BUSINESSIMAGE));
            download(map.get(TAG_RES_BUSINESSIMAGE),holder.businessImage);
        }
        if (holder.businessRating != null) {
            //new ImageDownloaderTask(holder.businessRating).execute(map.get(TAG_RES_RATINGIMAGE));
            download(map.get(TAG_RES_RATINGIMAGE), holder.businessRating);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView businessName;
        TextView businessAddress;
        ImageView businessImage;
        ImageView businessRating;
    }

    private void download(String url, ImageView imageView) {
        if (cancelPotentialDownload(url, imageView)) {
            ImageDownloaderTask task = new ImageDownloaderTask(imageView);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            task.execute(url);
        }
    }

    private static boolean cancelPotentialDownload(String url, ImageView imageView) {
        ImageDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) {
            String bitmapUrl = bitmapDownloaderTask.url;
            if ((bitmapUrl == null) || (!bitmapUrl.equals(url))) {
                bitmapDownloaderTask.cancel(true);
            } else {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    private static ImageDownloaderTask getBitmapDownloaderTask(ImageView imageView) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable) {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }


    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<HashMap<String, String>> filterList = new ArrayList<HashMap<String, String>>();
                for (int i = 0; i < filteredData.size(); i++) {
                    HashMap<String, String> map = (HashMap<String, String>) filteredData.get(i);
                    if ( map.get(TAG_RES_NAME).toUpperCase()
                            .contains(constraint.toString().toUpperCase())) {

                        filterList.add(map);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = filteredData.size();
                results.values = filteredData;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            listData = (ArrayList<HashMap<String, String>>) results.values;
            notifyDataSetChanged();
        }

    }
}
