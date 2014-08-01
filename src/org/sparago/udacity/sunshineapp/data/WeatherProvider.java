package org.sparago.udacity.sunshineapp.data;

import org.sparago.udacity.sunshineapp.data.WeatherContract.LocationEntry;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class WeatherProvider extends ContentProvider {
	private static final int WEATHER = 100;
	private static final int WEATHER_WITH_LOCATION = 101;
	private static final int WEATHER_WITH_LOCATION_AND_DATE = 102;
	private static final int LOCATION = 300;
	private static final int LOCATION_ID = 301;

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;
	// join between location and weather tables
	static {
		sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();
		sWeatherByLocationSettingQueryBuilder
				.setTables(WeatherContract.WeatherEntry.TABLE_NAME
						+ " INNER JOIN "
						+ WeatherContract.LocationEntry.TABLE_NAME + " ON "
						+ WeatherContract.WeatherEntry.TABLE_NAME + "."
						+ WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " = "
						+ WeatherContract.LocationEntry.TABLE_NAME + "."
						+ WeatherContract.LocationEntry._ID);
	}

	private static final String sLocationSettingSelection = WeatherContract.LocationEntry.TABLE_NAME
			+ "."
			+ WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
			+ " = ?";

	private static final String sLocationSettingWithStartDateSelection = WeatherContract.LocationEntry.TABLE_NAME
			+ "."
			+ WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
			+ " = ? AND "
			+ WeatherContract.WeatherEntry.COLUMN_DATETEXT
			+ " >= ?";

	private static final String sLocationSettingWithDaySelection = WeatherContract.LocationEntry.TABLE_NAME
			+ "."
			+ WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
			+ " = ? AND "
			+ WeatherContract.WeatherEntry.COLUMN_DATETEXT
			+ " = ?";

	private WeatherDbHelper dbHelper;

	@Override
	public boolean onCreate() {
		dbHelper = new WeatherDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor retCursor;

		final int match = sUriMatcher.match(uri);
		switch (match) {
		// "weather"
		case WEATHER: {
			retCursor = dbHelper.getReadableDatabase().query(
					WeatherContract.WeatherEntry.TABLE_NAME, projection,
					selection, selectionArgs, null, null, sortOrder);
			break;
		}
		// "weather/*"
		case WEATHER_WITH_LOCATION: {
			retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
			break;
		}
		// "weather/*/*"
		case WEATHER_WITH_LOCATION_AND_DATE: {
			retCursor = getWeatherByLocationSettingWithDate(uri, projection, sortOrder);
			break;
		}
		// location
		case LOCATION: {
			retCursor = dbHelper.getReadableDatabase().query(
					WeatherContract.LocationEntry.TABLE_NAME, projection,
					selection, selectionArgs, null, null, sortOrder);
			break;
		}
		// location/#
		case LOCATION_ID: {
			long id = ContentUris.parseId(uri);
			retCursor = dbHelper
					.getReadableDatabase()
					.query(WeatherContract.LocationEntry.TABLE_NAME,
							projection,
							(selection != null && selection.length() > 0 ? " and "
									: "")
									+ LocationEntry._ID
									+ " = "
									+ Long.toString(id), selectionArgs, null,
							null, sortOrder);
			break;
		}
		default:
			throw new UnsupportedOperationException("Unknown uri" + uri);
		}
		retCursor.setNotificationUri(getContext().getContentResolver(), uri);
		return retCursor;
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case WEATHER:
			return WeatherContract.WeatherEntry.CONTENT_TYPE;
		case WEATHER_WITH_LOCATION:
			return WeatherContract.WeatherEntry.CONTENT_TYPE;
		case WEATHER_WITH_LOCATION_AND_DATE:
			return WeatherContract.WeatherEntry.CONTENT_ITEM_TYPE;
		case LOCATION:
			return WeatherContract.LocationEntry.CONTENT_TYPE;
		case LOCATION_ID:
			return WeatherContract.LocationEntry.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri" + uri);
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		return null;
	}

	@Override
	public int delete(Uri uri, String s, String[] strings) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String s,
			String[] strings) {
		return 0;
	}

	private static UriMatcher buildUriMatcher() {
		final String authority = WeatherContract.CONTENT_AUTHORITY;

		UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

		matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
		matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*",
				WEATHER_WITH_LOCATION);
		matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/*",
				WEATHER_WITH_LOCATION_AND_DATE);

		matcher.addURI(authority, WeatherContract.PATH_LOCATION, LOCATION);
		matcher.addURI(authority, WeatherContract.PATH_LOCATION + "/#",
				LOCATION_ID);

		return matcher;
	}

	private Cursor getWeatherByLocationSetting(Uri uri, String[] projection,
			String sortOrder) {
		String locationSetting = WeatherContract.WeatherEntry
				.getLocationSettingFromUri(uri);
		String startDate = WeatherContract.WeatherEntry
				.getStartDateFromUri(uri);

		String[] selectionArgs;
		String selection;

		if (startDate == null) {
			selection = sLocationSettingSelection;
			selectionArgs = new String[] { locationSetting };
		} else {
			selection = sLocationSettingWithStartDateSelection;
			selectionArgs = new String[] { locationSetting, startDate };
		}
		return sWeatherByLocationSettingQueryBuilder.query(
				dbHelper.getReadableDatabase(), projection, selection,
				selectionArgs, null, null, sortOrder);
	}

	private Cursor getWeatherByLocationSettingWithDate(Uri uri,
			String[] projection, String sortOrder) {
		String day = WeatherContract.WeatherEntry.getDateFromUri(uri);
		String locationSetting = WeatherContract.WeatherEntry
				.getLocationSettingFromUri(uri);

		return sWeatherByLocationSettingQueryBuilder.query(
				dbHelper.getReadableDatabase(), projection,
				sLocationSettingWithDaySelection, new String[] {
						locationSetting, day }, null, null, sortOrder);
	}
}
