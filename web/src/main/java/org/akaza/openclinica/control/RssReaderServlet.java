/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.control;

import com.clinovo.util.SessionUtil;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.FetcherException;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.io.FeedException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.apache.commons.lang.StringEscapeUtils;
import org.jmesa.view.html.HtmlBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings({ "unchecked" })
public class RssReaderServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String rssUrl = SQLInitServlet.getField("rss.url");
		ResourceBundle resword = ResourceBundle.getBundle("org.akaza.openclinica.i18n.words",
				SessionUtil.getLocale(req));
		ResourceBundle resformat = ResourceBundle.getBundle("org.akaza.openclinica.i18n.format",
				SessionUtil.getLocale(req));
		PrintWriter pw = new PrintWriter(resp.getOutputStream());
		if (rssUrl == null || rssUrl.length() == 0) {
			about(resword, pw);
		} else {
			getFeed(resword, resformat, pw);
		}
	}

	void getFeed(ResourceBundle resword, ResourceBundle resformat, PrintWriter pw) {
		SyndFeed feed;
		String htmlFeed = null;
		try {
			String rssUrl = SQLInitServlet.getField("rss.url");
			FeedFetcher feedFetcher = new HttpURLFeedFetcher(HashMapFeedInfoCache.getInstance());
			feed = feedFetcher.retrieveFeed(new URL(rssUrl));
			htmlFeed = feedHtml(resword, resformat, feed);
		} catch (IllegalArgumentException e) {
			htmlFeed = errorFeedHtml(resword);
			e.printStackTrace();
		} catch (FeedException e) {
			htmlFeed = errorFeedHtml(resword);
			e.printStackTrace();
		} catch (FetcherException e) {
			htmlFeed = errorFeedHtml(resword);
			e.printStackTrace();
		} catch (Exception e) {
			htmlFeed = errorFeedHtml(resword);
			e.printStackTrace();
		} finally {
			pw.println(htmlFeed);
			pw.close();
		}
	}

	void about(ResourceBundle resword, PrintWriter pw) {
		String text1 = SQLInitServlet.getField("about.text1");
		String text2 = SQLInitServlet.getField("about.text2");
		HtmlBuilder htmlBuilder = new HtmlBuilder();
		htmlBuilder.h1().close().append(resword.getString("about")).h1End().ul().close();
		htmlBuilder.li().close().append(text1).liEnd();
		htmlBuilder.li().close().append(text2).liEnd();
		String html = htmlBuilder.ulEnd().toString();
		pw.println(html);
		pw.close();

	}

	String feedHtml(ResourceBundle resword, ResourceBundle resformat, SyndFeed feed) {
		String rssMore = SQLInitServlet.getField("rss.more");
		HtmlBuilder htmlBuilder = new HtmlBuilder();
		htmlBuilder.h1().close().append(resword.getString("news")).h1End().ul().close();
		List<SyndEntryImpl> theFeeds = feed.getEntries();

		for (int i = 0; i < (theFeeds.size() >= 4 ? 4 : theFeeds.size()); i++) {
			SyndEntryImpl syndFeed = theFeeds.get(i);
			String description;

			if (syndFeed.getDescription().getValue().length() > 50) {
				Integer k = 50;
				while (syndFeed.getDescription().getValue().charAt(k) != ' ') {
					k--;
				}
				description = syndFeed.getDescription().getValue().substring(0, k) + " ...";
			} else {
				description = syndFeed.getDescription().getValue();
			}
			SimpleDateFormat sdf = new SimpleDateFormat(resformat.getString("mid_date_format"));
			String theDate = sdf.format(syndFeed.getPublishedDate());
			htmlBuilder.li().close().a().href(syndFeed.getLink()).append(" target=\"_blank\"").close()
					.append(theDate + " - " + StringEscapeUtils.escapeHtml(syndFeed.getTitle()) + " - " + description)
					.aEnd().liEnd();

		}
		if (rssMore != null && rssMore.length() > 0) {
			return htmlBuilder.ulEnd().a().href(rssMore).append(" target=\"_blank\"").close().div().align("right")
					.close().append(resword.getString("more") + "...").divEnd().aEnd().toString();
		} else {
			return htmlBuilder.ulEnd().toString();
		}

	}

	String errorFeedHtml(ResourceBundle resword) {
		HtmlBuilder htmlBuilder = new HtmlBuilder();
		htmlBuilder.h1().close().append(resword.getString("news")).h1End().ul().close();
		htmlBuilder.li().close().append(resword.getString("couldnot_retrieve_news")).liEnd();
		return htmlBuilder.ulEnd().toString();
	}
}
