import { FC } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

type HeaderLinkProps = {
  text: string;
  href: string;
};

const StyledLink = styled(Link)`
  color: var(--ck-colors-text);
  text-decoration: none;
  font-size: var(--ck-font-sizes-lg);
  outline: none;
  transition: all 0.15s ease;

  &:hover {
    color: var(--ck-colors-text-hover);
  }

  &:active {
    color: var(--ck-colors-text-hover);
  }
`;

const HeaderLink: FC<HeaderLinkProps> = ({ text, href }) => {
  return <StyledLink to={href}>{text}</StyledLink>;
};

export default HeaderLink;
