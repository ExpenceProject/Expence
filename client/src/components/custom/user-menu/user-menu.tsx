import { Avatar } from '@/components/ui/avatar';
import {
  MenuContent,
  MenuItem,
  MenuRoot,
  MenuTrigger,
} from '@/components/ui/menu';
import { useUser } from '@/utils/providers/user-provider/use-user';
import { useRef } from 'react';
import { HiMiniArrowRight } from 'react-icons/hi2';
import { useNavigate } from 'react-router';

const UserMenu = () => {
  const { user, logout } = useUser();
  const avatarRef = useRef<HTMLDivElement>(null);
  const navigate = useNavigate();

  if (!user) {
    return null;
  }

  const getAnchorRect = () => {
    const avatar = avatarRef.current;

    if (!avatar) {
      return null;
    }

    return avatar.getBoundingClientRect();
  };

  const handleLogout = () => {
    logout().then(() => {
      navigate('/');
    });
  };

  return (
    <MenuRoot positioning={{ getAnchorRect }}>
      <MenuTrigger asChild>
        <Avatar
          ref={avatarRef}
          variant="solid"
          name={`${user?.firstName} ${user?.lastName}`}
          color="textBg"
          bg="primary"
          cursor="pointer"
        />
      </MenuTrigger>
      <MenuContent>
        <MenuItem value="profile" cursor="pointer">
          Profile
        </MenuItem>
        <MenuItem value="my-groups" cursor="pointer">
          My Groups
        </MenuItem>
        <MenuItem
          value="logout"
          color="textError"
          display="flex"
          gap={1}
          cursor="pointer"
          onClick={handleLogout}
        >
          Logout
          <HiMiniArrowRight />
        </MenuItem>
      </MenuContent>
    </MenuRoot>
  );
};

export default UserMenu;
