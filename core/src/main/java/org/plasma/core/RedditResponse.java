package org.plasma.core;

import com.google.gson.annotations.SerializedName;

// Main response from a reddit request 
public class RedditResponse {

	@SerializedName(value = "kind")
	private String kind;
	@SerializedName(value = "data")
	private RedditResponseData data;
	
	public String getKind() {
		return kind;
	}
	public void setKind(String kind) {
		this.kind = kind;
	}
	public RedditResponseData getData() {
		return data;
	}
	public void setData(RedditResponseData data) {
		this.data = data;
	}
	
	
}

/* Sample below 

{
"kind": "Listing",
"data": {
    "after": "t3_896z2v",
    "dist": 1,
    "modhash": null,
    "whitelist_status": "all_ads",
    "children": [
        {
            "kind": "t3",
            "data": {
                "subreddit_id": "t5_2qh3l",
                "approved_at_utc": null,
                "send_replies": true,
                "mod_reason_by": null,
                "banned_by": null,
                "num_reports": null,
                "removal_reason": null,
                "thumbnail_width": 140,
                "subreddit": "news",
                "selftext_html": null,
                "selftext": "",
                "likes": null,
                "suggested_sort": null,
                "user_reports": [],
                "secure_media": null,
                "is_reddit_media_domain": false,
                "saved": false,
                "id": "896z2v",
                "banned_at_utc": null,
                "mod_reason_title": null,
                "view_count": null,
                "archived": false,
                "clicked": false,
                "no_follow": false,
                "author": "Yuyumon",
                "num_crossposts": 2,
                "link_flair_text": "Soft paywall",
                "mod_reports": [],
                "can_mod_post": false,
                "is_crosspostable": true,
                "pinned": false,
                "score": 35320,
                "approved_by": null,
                "over_18": false,
                "report_reasons": null,
                "domain": "latimes.com",
                "hidden": false,
                "preview": {
                    "images": [
                        {
                            "source": {
                                "url": "https://i.redditmedia.com/70ixyFXaAG-ATLqE5FmUJJ6bcmfBl-nNCXhy93jRU4Y.jpg?s=9426fb98dcd368c924f230e1d32eb0f2",
                                "width": 1200,
                                "height": 674
                            },
                            "resolutions": [
                                {
                                    "url": "https://i.redditmedia.com/70ixyFXaAG-ATLqE5FmUJJ6bcmfBl-nNCXhy93jRU4Y.jpg?fit=crop&amp;crop=faces%2Centropy&amp;arh=2&amp;w=108&amp;s=4a607d9c3fa06c935bceed7b08a6edee",
                                    "width": 108,
                                    "height": 60
                                },
                                {
                                    "url": "https://i.redditmedia.com/70ixyFXaAG-ATLqE5FmUJJ6bcmfBl-nNCXhy93jRU4Y.jpg?fit=crop&amp;crop=faces%2Centropy&amp;arh=2&amp;w=216&amp;s=42db5f5ca2d7fb691dedc33c32cb88d0",
                                    "width": 216,
                                    "height": 121
                                },
                                {
                                    "url": "https://i.redditmedia.com/70ixyFXaAG-ATLqE5FmUJJ6bcmfBl-nNCXhy93jRU4Y.jpg?fit=crop&amp;crop=faces%2Centropy&amp;arh=2&amp;w=320&amp;s=05a0bd1778fb6967e594eba42dd50d61",
                                    "width": 320,
                                    "height": 179
                                },
                                {
                                    "url": "https://i.redditmedia.com/70ixyFXaAG-ATLqE5FmUJJ6bcmfBl-nNCXhy93jRU4Y.jpg?fit=crop&amp;crop=faces%2Centropy&amp;arh=2&amp;w=640&amp;s=6b842829742107f1f7ac2c9f44830a9d",
                                    "width": 640,
                                    "height": 359
                                },
                                {
                                    "url": "https://i.redditmedia.com/70ixyFXaAG-ATLqE5FmUJJ6bcmfBl-nNCXhy93jRU4Y.jpg?fit=crop&amp;crop=faces%2Centropy&amp;arh=2&amp;w=960&amp;s=4859135a67bd4c120ee2ce4a062b446e",
                                    "width": 960,
                                    "height": 539
                                },
                                {
                                    "url": "https://i.redditmedia.com/70ixyFXaAG-ATLqE5FmUJJ6bcmfBl-nNCXhy93jRU4Y.jpg?fit=crop&amp;crop=faces%2Centropy&amp;arh=2&amp;w=1080&amp;s=a0c13fadb14dd96fa7f8c1a286763bbe",
                                    "width": 1080,
                                    "height": 606
                                }
                            ],
                            "variants": {},
                            "id": "VWYNIGthxK-_s5DT6YsBSiD0wowAoaMFLd8kscbThT0"
                        }
                    ],
                    "enabled": false
                },
                "thumbnail": "default",
                "edited": false,
                "link_flair_css_class": "",
                "author_flair_css_class": null,
                "contest_mode": false,
                "gilded": 0,
                "downs": 0,
                "brand_safe": true,
                "secure_media_embed": {},
                "media_embed": {},
                "post_hint": "link",
                "author_flair_text": null,
                "stickied": false,
                "visited": false,
                "can_gild": true,
                "thumbnail_height": 78,
                "parent_whitelist_status": "all_ads",
                "name": "t3_896z2v",
                "spoiler": false,
                "permalink": "/r/news/comments/896z2v/saudi_crown_prince_says_israelis_palestinians/",
                "subreddit_type": "public",
                "locked": false,
                "hide_score": false,
                "created": 1522745870,
                "url": "http://www.latimes.com/world/middleeast/la-fg-saudi-israel-20180402-story.html",
                "whitelist_status": "all_ads",
                "quarantine": false,
                "subreddit_subscribers": 15850824,
                "created_utc": 1522717070,
                "subreddit_name_prefixed": "r/news",
                "ups": 35320,
                "media": null,
                "num_comments": 2490,
                "is_self": false,
                "title": "Saudi crown prince says Israelis, Palestinians both have 'right to have their own land'",
                "mod_note": null,
                "is_video": false,
                "distinguished": null
            }
        }
    ],
    "before": null
}
}

*/