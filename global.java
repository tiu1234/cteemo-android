package com.example.duang;

import org.json.JSONObject;

class global{
	public static final int init_mode = -3;
	public static final int profile_mode2 = -2;
	public static final int login_mode = -1;
	public static final int select_mode = 0;
	public static final int signup_mode = 1;
	public static final int profile_mode = 2;
	public static final int logged_in_mode = 3;
	public static final int team_mode = 4;
	public static final int signup = 5;
	public static final int login = 6;
	public static final int fb_login = 7;
	public static final int profile = 8;
	public static final int profile_icon = 9;
	public static final int download_profile_icon = 10;
	public static final int get_profile = 11;
	public static final int get_team_profile = 12;
	public static final int get_html = 13;
	public static final int get_news_list = 14;
	public static final int create_team = 15;
	public static final int team_icon = 16;
	public static final int rename_team = 17;
	public static final int leave_team = 18;
	public static final int dismiss_team = 19;
	public static final int join_team = 20;
	public static final int search_team = 21;
	public static final int forgot_pw = 22;
	public static final int search_player = 23;
	public static final int add_player = 24;
	public static final int contact_player = 25;
	public static final int me_mode = 26;
	public static final int tournament_mode = 27;
	public static final int join_tournament = 28;
	public static final int show_tournament = 29;
	public static final int index_tournament = 30;
	public static final int summoner_id = 31;
	public static final int summoner_entry = 32;
	public static final int view_tournament = 33;
	public static final int search_tournaments = 34;
	public static final int check_joined = 35;
	public static final int check_in = 36;
	public static final int participants_tournament = 37;
	public static final int send_tournamentcode = 38;
	public static final int match_tournament = 39;
	public static final int search_match = 40;
	public static final int current_game = 41;
	public static final int view_team = 42;
	public static final int view_profile = 43;
	public static final int recent_games = 44;
	public static final int match_result = 45;
	public static final int match_attachment = 46;
	public static int mode = logged_in_mode;
	public static String token = "";
	public static boolean fb_flag = false;
	public static String icon_url = "";
	public static String team_icon_url = "";
	public static JSONObject user_json;
	public static JSONObject team_json;
	public static String summonerid;
	public static String summonername;
	public static String summonerlevel;
	public static String solo_tier;
	public static String school;
	public static int width;
	public static int height;
	public static boolean tournament_code_sent = false;
	public static int check_status = 0;
	public static int other_participant_id;
	public static volatile boolean out_loop = true;
	public static int start_game = 0;
	public static String other_captain_id = "";
}