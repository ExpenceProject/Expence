import { FC } from 'react';
import { Link } from 'react-router-dom';
import styled from 'styled-components';

type HeaderLinkProps = {
  text: string;
  href: string;
  onClick?: () => void;
};

const StyledLink = styled(Link)`
  color: var(--ck-colors-text);
  text-decoration: none;
  font-size: var(--ck-font-sizes-lg);
  outline: none;
  transition: all 0.15s ease;
  background-color: var(--ck-colors-background);
  padding: 10px 15px;
  width: 100%;
  text-align: center;
  border-radius: 10px;

  &:hover {
    color: var(--ck-colors-text-hover);
    background-color: var(--ck-colors-hover);
  }

  &:active {
    color: var(--ck-colors-text-hover);
  }
`;

const HeaderLink: FC<HeaderLinkProps> = ({ text, href, onClick }) => {
  return (
    <StyledLink to={href} onClick={onClick}>
      {text}
    </StyledLink>
  );
};

export default HeaderLink;
