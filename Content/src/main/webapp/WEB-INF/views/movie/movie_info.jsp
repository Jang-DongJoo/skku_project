<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Recommend</title>
</head>
<body>
	<!-- 1) 검색폼 구성 -->
	<form name="form1" method="get"
		action="<%=request.getContextPath()%>/movie/movie_info.do">
		<label for="movieNm">영화제목: </label> <input type="text" id="movieNm"
			name="movieNm" value="${movieNm}" /> <input type="submit" value="검색" />
	</form>

	<table border="1">
		<thead>
			<tr>
				<th>Movie Name</th>
				<th>Similarity</th>
			</tr>
		</thead>
		<tbody><c:choose>
                <%-- 조회결과가 없는 경우 --%>
                <c:when test="${it == null || fn:length(it) == 0}">
                    <tr>
                        <td colspan="3" align="center">조회결과가 없습니다.</td>
                    </tr>
                </c:when>
                <%-- 조회결과가 있는  경우 --%>
                <c:otherwise>
                    <%-- 조회 결과에 따른 반복 처리 --%>
                    <c:forEach var="item" items="${it}" varStatus="status">
                        <%-- 출력을 위해 준비한 학과이름과 위치 --%>
                        <c:set var="movieNm" value="${it.next()}" />
                        <c:set var="similarity" value="${map.get(it.next())}" />
                        
                        <tr>
                            <td align="center">${movieNm}</a></td>
                            <td align="center">${similarity}</td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
		</tbody>
	</table>

</body>
</html>