import { useState } from 'react';
import Header from './header/Header';
import SidebarMenu from './sidebar/SidebarMenu';
import './Layout.css';

/**
 * Layout component – main app wrapper that includes the top header and the sidebar.
 * Controls whether the sidebar is open or closed.
 */
const Layout = ({ children }) => {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  return (
    <div className="app-layout">
      {/* Top bar */}
      <Header onToggleSidebar={() => setIsSidebarOpen(prev => !prev)} />

      <div className="layout-body">
        {/* Left sidebar – show/hide based on toggle */}
        {isSidebarOpen && <SidebarMenu />}

        {/* Main content area */}
        <main className="layout-content">
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;
