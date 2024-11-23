import { Flex } from '@chakra-ui/react';
import { FC } from 'react';

import HeaderLink from './header-link';

type HeaderNavProps = {
  direction?: 'row' | 'column';
};

const HeaderNav: FC<HeaderNavProps> = ({ direction = 'row' }) => {
  return (
    <Flex flexDirection={direction} align="center" justify="center" gap="8">
      <HeaderLink text="Tracker" href="/" />
      <HeaderLink text="About" href="/" />
    </Flex>
  );
};

export default HeaderNav;
