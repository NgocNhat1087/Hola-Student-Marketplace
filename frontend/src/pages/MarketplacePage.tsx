import { Link } from 'react-router-dom';
import { ResetDemoButton, ucPages } from './UseCasePages';
export function MarketplacePage() {
 return <div className="container page"><div className="directory-heading"><p className="eyebrow">HOLA STUDENT MARKETPLACE · FEATURES</p><h1>Explore your <em>community.</em></h1><p>Demo dữ liệu mẫu: thử bình luận, yêu thích, theo dõi, xem thông báo và báo cáo vi phạm. Không cần đăng nhập hoặc chạy backend.</p><ResetDemoButton /></div><div className="feature-grid">{ucPages.map(x=><Link className="feature-card" key={x.path} to={x.path}><span className="feature-icon">{x.icon}</span><small>{x.uc}</small><h2>{x.title}</h2><p>{x.description}</p><strong>Open feature →</strong></Link>)}</div></div>;
}
