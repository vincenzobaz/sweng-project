package ch.epfl.sweng.jassatepfl.tools;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import ch.epfl.sweng.jassatepfl.R;
import ch.epfl.sweng.jassatepfl.model.Match;

/**
 * Custom {@link android.widget.Adapter Adapter} used to display
 * matches in the {@link android.widget.ListView ListView} of
 * {@link ch.epfl.sweng.jassatepfl.MatchListActivity MatchListActivity}.
 */
public class CustomAdapter extends BaseAdapter {

    private final Context context;
    private final List<Match> matches;

    public CustomAdapter(Context context, List<Match> matches) {
        this.context = context;
        this.matches = matches;
    }

    @Override
    public int getCount() {
        return matches.size();
    }

    @Override
    public Object getItem(int position) {
        return matches.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.match_list_row, null);
        }

        TextView description = (TextView) convertView.findViewById(R.id.description);
        TextView rankData = (TextView) convertView.findViewById(R.id.rank_data);
        TextView players = (TextView) convertView.findViewById(R.id.players_data);
        TextView variant = (TextView) convertView.findViewById(R.id.variant_data);
        TextView expirationDate = (TextView) convertView.findViewById(R.id.expiration_date_data);

        Match match = matches.get(position);
        MatchStringifier stringifier = new MatchStringifier(context);
        stringifier.setMatch(match);

        description.setText(match.getDescription());
        rankData.setText(stringifier.rankToString());
        players.setText(stringifier.playersToString());
        variant.setText(stringifier.variantToString());
        expirationDate.setText(stringifier.dateToStringCustom());

        return convertView;
    }

}
