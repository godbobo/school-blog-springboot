package cn.bzeal.schoolblog.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtTokenUtil {

	public static String SECRET = "0school1blog2";


	public static String createToken(String id, int role, String name) throws Exception {
		//签发时间
		Date istDate = new Date();

		//设置过期时间 2周
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(Calendar.DAY_OF_MONTH, 14);
		Date expiresDate = nowTime.getTime();

		// header 设置
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");

		String token = JWT.create()
				.withHeader(map)
				.withClaim("name", name)
				.withClaim("id", id)
				.withClaim("role", role)
				.withExpiresAt(expiresDate)
				.withIssuedAt(istDate)
				.sign(Algorithm.HMAC256(SECRET));

		return token;
	}


	public static Map<String, Claim> verifyToken(String token) throws Exception {
		JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
		DecodedJWT jwt = null;
		try {
			jwt = verifier.verify(token);
		} catch (Exception e) {
			throw new RuntimeException("凭证过期！");
		}

		return jwt.getClaims();
	}
}